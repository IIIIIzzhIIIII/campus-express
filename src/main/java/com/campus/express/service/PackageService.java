package com.campus.express.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.ActionType;
import com.campus.express.common.BusinessException;
import com.campus.express.common.NoticeType;
import com.campus.express.common.PackageStatus;
import com.campus.express.common.PickupType;
import com.campus.express.dto.PackageInDTO;
import com.campus.express.dto.PickupDTO;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.entity.NoticeRecord;
import com.campus.express.entity.Package;
import com.campus.express.entity.PackageLog;
import com.campus.express.entity.Shelf;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.ExpressCompanyMapper;
import com.campus.express.mapper.NoticeRecordMapper;
import com.campus.express.mapper.PackageLogMapper;
import com.campus.express.mapper.PackageMapper;
import com.campus.express.mapper.ShelfMapper;
import com.campus.express.mapper.SysUserMapper;
import com.campus.express.util.PickupCodeUtil;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.PackageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 快件服务（核心业务）
 */
@Slf4j
@Service
public class PackageService {

    @Resource
    private PackageMapper packageMapper;

    @Resource
    private PackageLogMapper packageLogMapper;

    @Resource
    private NoticeRecordMapper noticeRecordMapper;

    @Resource
    private ExpressCompanyMapper companyMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ShelfMapper shelfMapper;

    @Value("${campus.pickup-code-length:6}")
    private int pickupCodeLength;

    /**
     * 快件入库登记
     */
    public PackageVO inbound(PackageInDTO dto) {
        Long count = packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .eq(Package::getTrackingNo, dto.getTrackingNo().trim()));
        if (count > 0) {
            throw new BusinessException("该快递单号已入库，请勿重复登记");
        }

        Package pkg = new Package();
        pkg.setTrackingNo(dto.getTrackingNo().trim());
        pkg.setCompanyId(dto.getCompanyId());
        pkg.setReceiverName(dto.getReceiverName().trim());
        pkg.setReceiverPhone(dto.getReceiverPhone().trim());
        pkg.setPickupCode(generateUniquePickupCode());
        pkg.setStatus(PackageStatus.IN.getCode());
        pkg.setOperatorId(SecurityUtils.getLoginUserId());
        pkg.setRemark(dto.getRemark());
        pkg.setNoticeCount(0);

        String shelfNo = StrUtil.isBlank(dto.getShelfNo()) ? autoAssignShelf() : dto.getShelfNo().trim();
        pkg.setShelfNo(shelfNo);

        packageMapper.insert(pkg);
        saveLog(pkg, ActionType.IN, null, PackageStatus.IN.getCode(), "快件入库登记，货架 " + shelfNo);
        return toVO(pkg);
    }

    /**
     * 分页查询
     */
    public IPage<PackageVO> page(int page, int size, String keyword, Integer status,
                                 Long companyId, String dateStart, String dateEnd) {
        LambdaQueryWrapper<Package> wrapper = new LambdaQueryWrapper<Package>()
                .orderByDesc(Package::getInTime);
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(Package::getTrackingNo, k)
                    .or().like(Package::getReceiverName, k)
                    .or().like(Package::getReceiverPhone, k)
                    .or().like(Package::getPickupCode, k));
        }
        if (status != null) {
            wrapper.eq(Package::getStatus, status);
        }
        if (companyId != null) {
            wrapper.eq(Package::getCompanyId, companyId);
        }
        if (StrUtil.isNotBlank(dateStart)) {
            wrapper.ge(Package::getInTime, LocalDate.parse(dateStart).atStartOfDay());
        }
        if (StrUtil.isNotBlank(dateEnd)) {
            wrapper.le(Package::getInTime, LocalDate.parse(dateEnd).atTime(LocalTime.MAX));
        }
        IPage<Package> pageResult = packageMapper.selectPage(new Page<>(page, size), wrapper);
        return pageResult.convert(this::toVO);
    }

    /**
     * 详情
     */
    public PackageVO detail(Long id) {
        Package pkg = packageMapper.selectById(id);
        if (pkg == null) {
            throw new BusinessException("快件不存在");
        }
        return toVO(pkg);
    }

    /**
     * 快件操作日志
     */
    public List<PackageLog> logs(Long packageId) {
        return packageLogMapper.selectList(new LambdaQueryWrapper<PackageLog>()
                .eq(PackageLog::getPackageId, packageId)
                .orderByAsc(PackageLog::getCreateTime));
    }

    /**
     * 发送取件通知
     */
    public PackageVO notice(Long id) {
        Package pkg = packageMapper.selectById(id);
        if (pkg == null) {
            throw new BusinessException("快件不存在");
        }
        if (pkg.getStatus() != PackageStatus.IN.getCode() && pkg.getStatus() != PackageStatus.STAY.getCode()) {
            throw new BusinessException("当前状态不可发送通知");
        }
        Integer before = pkg.getStatus();
        pkg.setStatus(PackageStatus.NOTICED.getCode());
        pkg.setNoticeTime(LocalDateTime.now());
        pkg.setNoticeCount(pkg.getNoticeCount() == null ? 1 : pkg.getNoticeCount() + 1);
        packageMapper.updateById(pkg);

        String content = "【校园驿站】您有快递已到，取件码 " + pkg.getPickupCode()
                + "，货架 " + pkg.getShelfNo() + "，请凭码取件。";
        NoticeRecord record = new NoticeRecord();
        record.setPackageId(pkg.getId());
        record.setTrackingNo(pkg.getTrackingNo());
        record.setReceiverPhone(pkg.getReceiverPhone());
        record.setNoticeType(NoticeType.SMS);
        record.setContent(content);
        record.setSendStatus(1);
        noticeRecordMapper.insert(record);
        log.info("【模拟短信】发送至 {}：{}", pkg.getReceiverPhone(), content);

        saveLog(pkg, ActionType.NOTICE, before, PackageStatus.NOTICED.getCode(), "发送取件通知");
        return toVO(pkg);
    }

    /**
     * 批量通知
     */
    public int noticeBatch(List<Long> ids) {
        int success = 0;
        for (Long id : ids) {
            try {
                notice(id);
                success++;
            } catch (Exception e) {
                log.warn("批量通知失败 id={}, {}", id, e.getMessage());
            }
        }
        return success;
    }

    /**
     * 取件办理
     */
    public PackageVO pickup(PickupDTO dto) {
        Package pkg = findForPickup(dto);
        if (pkg == null) {
            throw new BusinessException("未找到匹配的快件，请核对取件信息");
        }
        Integer status = pkg.getStatus();
        if (status == PackageStatus.PICKED.getCode()) {
            throw new BusinessException("该快件已取件，请勿重复操作");
        }
        if (status != PackageStatus.NOTICED.getCode() && status != PackageStatus.STAY.getCode()) {
            throw new BusinessException("请先发送取件通知后再办理取件");
        }

        pkg.setStatus(PackageStatus.PICKED.getCode());
        pkg.setPickupTime(LocalDateTime.now());
        pkg.setPickupType(dto.getType());
        packageMapper.updateById(pkg);

        String typeDesc = PickupType.descOf(dto.getType());
        saveLog(pkg, ActionType.PICKUP, status, PackageStatus.PICKED.getCode(), "取件确认（" + typeDesc + "）");
        return toVO(pkg);
    }

    /**
     * 删除（逻辑删除，仅超级管理员）
     */
    public void delete(Long id) {
        Package pkg = packageMapper.selectById(id);
        if (pkg == null) {
            throw new BusinessException("快件不存在");
        }
        if (pkg.getStatus() == PackageStatus.PICKED.getCode()) {
            throw new BusinessException("已取件的快件不可删除");
        }
        packageMapper.deleteById(id);
    }

    /**
     * 学生自助查询（按手机号或快递单号）
     */
    public List<PackageVO> studentQuery(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            throw new BusinessException("请输入手机号或快递单号");
        }
        String k = keyword.trim();
        LambdaQueryWrapper<Package> wrapper = new LambdaQueryWrapper<Package>()
                .and(w -> w.eq(Package::getReceiverPhone, k).or().eq(Package::getTrackingNo, k))
                .orderByDesc(Package::getInTime);
        return packageMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    /**
     * 学生端「我的快递」：按登录手机号查询收件记录
     */
    public List<PackageVO> listByReceiverPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return List.of();
        }
        return packageMapper.selectList(new LambdaQueryWrapper<Package>()
                        .eq(Package::getReceiverPhone, phone.trim())
                        .orderByDesc(Package::getInTime))
                .stream().map(this::toVO).toList();
    }

    private Package findForPickup(PickupDTO dto) {
        String type = dto.getType();
        if (PickupType.PICKUP_CODE.getCode().equals(type)) {
            if (StrUtil.isBlank(dto.getPickupCode())) {
                throw new BusinessException("请输入取件码");
            }
            List<Package> list = packageMapper.selectList(new LambdaQueryWrapper<Package>()
                    .eq(Package::getPickupCode, dto.getPickupCode().trim())
                    .orderByDesc(Package::getId));
            return list.isEmpty() ? null : list.get(0);
        }
        if (PickupType.SCAN.getCode().equals(type)) {
            if (StrUtil.isBlank(dto.getTrackingNo())) {
                throw new BusinessException("请扫描或输入快递单号");
            }
            return packageMapper.selectOne(new LambdaQueryWrapper<Package>()
                    .eq(Package::getTrackingNo, dto.getTrackingNo().trim()));
        }
        if (PickupType.PHONE_TAIL.getCode().equals(type)) {
            if (StrUtil.isBlank(dto.getTrackingNo()) || StrUtil.isBlank(dto.getPhone())) {
                throw new BusinessException("请输入快递单号和手机号后四位");
            }
            Package pkg = packageMapper.selectOne(new LambdaQueryWrapper<Package>()
                    .eq(Package::getTrackingNo, dto.getTrackingNo().trim()));
            if (pkg == null) {
                return null;
            }
            String phone = dto.getPhone().trim();
            String tail = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
            String receiverPhone = pkg.getReceiverPhone();
            if (receiverPhone == null || !receiverPhone.endsWith(tail)) {
                throw new BusinessException("手机号后四位与收件人不符");
            }
            return pkg;
        }
        throw new BusinessException("不支持的取件方式");
    }

    private String generateUniquePickupCode() {
        for (int i = 0; i < 20; i++) {
            String code = PickupCodeUtil.generate(pickupCodeLength);
            Long count = packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                    .eq(Package::getPickupCode, code));
            if (count == 0) {
                return code;
            }
        }
        return String.valueOf(System.currentTimeMillis()).substring(6);
    }

    private String autoAssignShelf() {
        List<Shelf> shelves = shelfMapper.selectList(new LambdaQueryWrapper<Shelf>()
                .eq(Shelf::getStatus, 1)
                .orderByAsc(Shelf::getShelfNo)
                .last("limit 1"));
        return shelves.isEmpty() ? "未分配" : shelves.get(0).getShelfNo();
    }

    private void saveLog(Package pkg, String action, Integer before, Integer after, String content) {
        PackageLog logEntity = new PackageLog();
        logEntity.setPackageId(pkg.getId());
        logEntity.setTrackingNo(pkg.getTrackingNo());
        logEntity.setAction(action);
        logEntity.setBeforeStatus(before);
        logEntity.setAfterStatus(after);
        logEntity.setContent(content);
        SysUser user = SecurityUtils.getLoginUser();
        logEntity.setOperatorId(user == null ? null : user.getId());
        logEntity.setOperatorName(user == null ? null : user.getRealName());
        packageLogMapper.insert(logEntity);
    }

    private PackageVO toVO(Package pkg) {
        PackageVO vo = new PackageVO();
        BeanUtil.copyProperties(pkg, vo);
        vo.setStatusDesc(PackageStatus.descOf(pkg.getStatus()));
        vo.setPickupTypeDesc(PickupType.descOf(pkg.getPickupType()));
        if (pkg.getCompanyId() != null) {
            ExpressCompany company = companyMapper.selectById(pkg.getCompanyId());
            vo.setCompanyName(company == null ? null : company.getCompanyName());
        }
        if (pkg.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(pkg.getOperatorId());
            vo.setOperatorName(user == null ? null : user.getRealName());
        }
        return vo;
    }
}


