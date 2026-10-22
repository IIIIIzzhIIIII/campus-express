package com.campus.express.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.BusinessException;
import com.campus.express.common.SendStatus;
import com.campus.express.dto.SendCollectDTO;
import com.campus.express.dto.SendOrderDTO;
import com.campus.express.dto.SendTrackDTO;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.entity.SendOrder;
import com.campus.express.entity.SendOrderLog;
import com.campus.express.entity.StudentUser;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.ExpressCompanyMapper;
import com.campus.express.mapper.SendOrderLogMapper;
import com.campus.express.mapper.SendOrderMapper;
import com.campus.express.mapper.StudentUserMapper;
import com.campus.express.mapper.SysUserMapper;
import com.campus.express.util.DateParseUtil;
import com.campus.express.util.OrderNoUtil;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.SendOrderVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寄件订单服务（用户端下单 + 驿站揽收/物流跟踪）
 */
@Service
public class SendOrderService {

    @Resource
    private SendOrderMapper sendOrderMapper;

    @Resource
    private SendOrderLogMapper sendOrderLogMapper;

    @Resource
    private ExpressCompanyMapper companyMapper;

    @Resource
    private StudentUserMapper studentUserMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 学生下单寄件
     */
    public SendOrderVO create(SendOrderDTO dto) {
        StudentUser student = requireStudent();

        SendOrder order = new SendOrder();
        order.setOrderNo(OrderNoUtil.generate("S"));
        order.setStudentId(student.getId());
        order.setCompanyId(dto.getCompanyId());
        order.setSenderName(trim(dto.getSenderName(), student.getRealName()));
        order.setSenderPhone(trim(dto.getSenderPhone(), student.getPhone()));
        order.setSenderAddress(dto.getSenderAddress().trim());
        order.setReceiverName(dto.getReceiverName().trim());
        order.setReceiverPhone(dto.getReceiverPhone().trim());
        order.setReceiverAddress(dto.getReceiverAddress().trim());
        order.setItemName(dto.getItemName().trim());
        order.setWeight(dto.getWeight());
        order.setExpectTime(DateParseUtil.parse(dto.getExpectTime()));
        order.setRemark(dto.getRemark());
        order.setStatus(SendStatus.WAIT_COLLECT.getCode());
        sendOrderMapper.insert(order);

        saveLog(order, SendStatus.WAIT_COLLECT.getCode(),
                "寄件订单已提交，等待驿站揽收", "校园快递驿站", student.getRealName());
        return toVO(order);
    }

    /**
     * 学生「我的寄件」分页
     */
    public IPage<SendOrderVO> pageByStudent(int page, int size, Integer status) {
        StudentUser student = requireStudent();
        LambdaQueryWrapper<SendOrder> wrapper = new LambdaQueryWrapper<SendOrder>()
                .eq(SendOrder::getStudentId, student.getId())
                .orderByDesc(SendOrder::getId);
        if (status != null) {
            wrapper.eq(SendOrder::getStatus, status);
        }
        return sendOrderMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    /**
     * 学生「我的收件」分页：按下单学生手机号匹配收件人手机号，
     * 即「别人在线寄给我的寄件订单」，收件人凭此可跟踪物流。
     */
    public IPage<SendOrderVO> pageByReceiver(int page, int size, Integer status) {
        StudentUser student = requireStudent();
        if (StrUtil.isBlank(student.getPhone())) {
            return new Page<>(page, size);
        }
        LambdaQueryWrapper<SendOrder> wrapper = new LambdaQueryWrapper<SendOrder>()
                .eq(SendOrder::getReceiverPhone, student.getPhone().trim())
                .orderByDesc(SendOrder::getId);
        if (status != null) {
            wrapper.eq(SendOrder::getStatus, status);
        }
        return sendOrderMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    /**
     * 按手机号 / 运单号 / 寄件单号查询寄件订单（学生免登录自助查询用）
     */
    public List<SendOrderVO> queryByKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }
        String k = keyword.trim();
        return sendOrderMapper.selectList(new LambdaQueryWrapper<SendOrder>()
                        .and(w -> w.eq(SendOrder::getReceiverPhone, k)
                                .or().eq(SendOrder::getTrackingNo, k)
                                .or().eq(SendOrder::getOrderNo, k))
                        .orderByDesc(SendOrder::getId))
                .stream().map(this::toVO).toList();
    }

    /**
     * 学生查看寄件详情（下单人本人或收件人均可查看）
     */
    public SendOrderVO detailForStudent(Long id) {
        return toVO(findAccessibleOrder(id));
    }

    /**
     * 寄件物流轨迹（学生端，下单人本人或收件人均可查看）
     */
    public List<SendOrderLog> logsForStudent(Long orderId) {
        findAccessibleOrder(orderId);
        return logs(orderId);
    }

    /**
     * 学生取消寄件（仅待揽收可取消）
     */
    public SendOrderVO cancelByStudent(Long id) {
        SendOrder order = findOwnOrder(id);
        if (!SendStatus.cancelable(order.getStatus())) {
            throw new BusinessException("驿站已揽收，无法取消，请联系驿站处理");
        }
        order.setStatus(SendStatus.CANCELED.getCode());
        sendOrderMapper.updateById(order);
        saveLog(order, SendStatus.CANCELED.getCode(), "学生取消寄件订单", "校园快递驿站", order.getSenderName());
        return toVO(order);
    }

    /**
     * 后台分页查询
     */
    public IPage<SendOrderVO> page(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<SendOrder> wrapper = new LambdaQueryWrapper<SendOrder>()
                .orderByDesc(SendOrder::getId);
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(SendOrder::getOrderNo, k)
                    .or().like(SendOrder::getTrackingNo, k)
                    .or().like(SendOrder::getSenderName, k)
                    .or().like(SendOrder::getSenderPhone, k)
                    .or().like(SendOrder::getReceiverName, k)
                    .or().like(SendOrder::getReceiverPhone, k));
        }
        if (status != null) {
            wrapper.eq(SendOrder::getStatus, status);
        }
        return sendOrderMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    /**
     * 后台查看寄件详情
     */
    public SendOrderVO detail(Long id) {
        SendOrder order = sendOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("寄件订单不存在");
        }
        return toVO(order);
    }

    /**
     * 物流轨迹（后台 / 学生共用）
     */
    public List<SendOrderLog> logs(Long orderId) {
        return sendOrderLogMapper.selectList(new LambdaQueryWrapper<SendOrderLog>()
                .eq(SendOrderLog::getOrderId, orderId)
                .orderByAsc(SendOrderLog::getCreateTime)
                .orderByAsc(SendOrderLog::getId));
    }

    /**
     * 驿站揽收：回填运单号
     */
    public SendOrderVO collect(Long id, SendCollectDTO dto) {
        SendOrder order = sendOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("寄件订单不存在");
        }
        if (!SendStatus.cancelable(order.getStatus())) {
            throw new BusinessException("该订单已揽收，请勿重复操作");
        }
        String trackingNo = dto.getTrackingNo().trim();
        order.setTrackingNo(trackingNo);
        order.setStatus(SendStatus.COLLECTED.getCode());
        sendOrderMapper.updateById(order);

        String location = StrUtil.blankToDefault(dto.getLocation(), "校园快递驿站");
        saveLog(order, SendStatus.COLLECTED.getCode(),
                "驿站已揽收，运单号 " + trackingNo, location, operatorName());
        return toVO(order);
    }

    /**
     * 驿站更新物流状态（运输中 / 派送中 / 已签收）
     */
    public SendOrderVO updateStatus(SendTrackDTO dto) {
        SendOrder order = sendOrderMapper.selectById(dto.getId());
        if (order == null) {
            throw new BusinessException("寄件订单不存在");
        }
        SendStatus target = null;
        for (SendStatus s : SendStatus.values()) {
            if (s.getCode().equals(dto.getStatus())) {
                target = s;
                break;
            }
        }
        if (target == null || target.getCode() < SendStatus.IN_TRANSIT.getCode()
                || target.getCode() > SendStatus.SIGNED.getCode()) {
            throw new BusinessException("仅支持更新为运输中 / 派送中 / 已签收");
        }
        if (order.getStatus() == null || order.getStatus() >= target.getCode()) {
            throw new BusinessException("当前状态不允许更新为「" + target.getDesc() + "」");
        }
        if (StrUtil.isBlank(order.getTrackingNo())) {
            throw new BusinessException("请先揽收并回填运单号");
        }
        order.setStatus(target.getCode());
        sendOrderMapper.updateById(order);

        String location = StrUtil.blankToDefault(dto.getLocation(), "校园快递驿站");
        String content = StrUtil.blankToDefault(dto.getContent(), "快件已" + target.getDesc());
        saveLog(order, target.getCode(), content, location, operatorName());
        return toVO(order);
    }

    /**
     * 驿站取消寄件订单
     */
    public SendOrderVO cancel(Long id) {
        SendOrder order = sendOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("寄件订单不存在");
        }
        if (SendStatus.SIGNED.getCode().equals(order.getStatus())
                || SendStatus.CANCELED.getCode().equals(order.getStatus())) {
            throw new BusinessException("该订单已结束，无法取消");
        }
        order.setStatus(SendStatus.CANCELED.getCode());
        sendOrderMapper.updateById(order);
        saveLog(order, SendStatus.CANCELED.getCode(), "驿站取消寄件订单", "校园快递驿站", operatorName());
        return toVO(order);
    }

    /**
     * 校验归属：仅下单学生本人可操作（如取消寄件）
     */
    private SendOrder findOwnOrder(Long id) {
        StudentUser student = requireStudent();
        SendOrder order = sendOrderMapper.selectById(id);
        if (order == null || !student.getId().equals(order.getStudentId())) {
            throw new BusinessException("寄件订单不存在");
        }
        return order;
    }

    /**
     * 校验可读：下单学生本人 或 收件手机号匹配的收件学生 均可查看详情 / 物流
     */
    private SendOrder findAccessibleOrder(Long id) {
        StudentUser student = requireStudent();
        SendOrder order = sendOrderMapper.selectById(id);
        if (order == null
                || !(student.getId().equals(order.getStudentId())
                || StrUtil.equals(student.getPhone(), order.getReceiverPhone()))) {
            throw new BusinessException("寄件订单不存在");
        }
        return order;
    }

    private StudentUser requireStudent() {
        StudentUser session = SecurityUtils.getLoginStudent();
        if (session == null) {
            throw new BusinessException(401, "请先登录学生账号");
        }
        StudentUser student = studentUserMapper.selectById(session.getId());
        if (student == null) {
            throw new BusinessException(401, "账号不存在，请重新登录");
        }
        return student;
    }

    private String operatorName() {
        SysUser user = SecurityUtils.getLoginUser();
        return user == null ? "驿站" : StrUtil.blankToDefault(user.getRealName(), user.getUsername());
    }

    private String trim(String value, String fallback) {
        return StrUtil.isBlank(value) ? fallback : value.trim();
    }

    private void saveLog(SendOrder order, Integer status, String content, String location, String operator) {
        SendOrderLog log = new SendOrderLog();
        log.setOrderId(order.getId());
        log.setStatus(status);
        log.setContent(content);
        log.setLocation(location);
        log.setOperator(operator);
        sendOrderLogMapper.insert(log);
    }

    private SendOrderVO toVO(SendOrder order) {
        SendOrderVO vo = new SendOrderVO();
        BeanUtil.copyProperties(order, vo);
        vo.setStatusDesc(SendStatus.descOf(order.getStatus()));
        if (order.getCompanyId() != null) {
            ExpressCompany company = companyMapper.selectById(order.getCompanyId());
            vo.setCompanyName(company == null ? null : company.getCompanyName());
        }
        if (order.getStudentId() != null) {
            StudentUser student = studentUserMapper.selectById(order.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getRealName());
                vo.setStudentPhone(student.getPhone());
            }
        }
        return vo;
    }
}
