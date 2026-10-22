package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.ActionType;
import com.campus.express.common.BusinessException;
import com.campus.express.common.HandleStatus;
import com.campus.express.common.PackageStatus;
import com.campus.express.common.ProblemType;
import com.campus.express.dto.ProblemDTO;
import com.campus.express.dto.ProblemHandleDTO;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.entity.Package;
import com.campus.express.entity.PackageLog;
import com.campus.express.entity.ProblemPackage;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.ExpressCompanyMapper;
import com.campus.express.mapper.PackageLogMapper;
import com.campus.express.mapper.PackageMapper;
import com.campus.express.mapper.ProblemPackageMapper;
import com.campus.express.mapper.SysUserMapper;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.ProblemVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 问题件服务
 */
@Service
public class ProblemService {

    @Resource
    private ProblemPackageMapper problemMapper;

    @Resource
    private PackageMapper packageMapper;

    @Resource
    private PackageLogMapper packageLogMapper;

    @Resource
    private ExpressCompanyMapper companyMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 登记问题件
     */
    public void register(ProblemDTO dto) {
        Package pkg = packageMapper.selectById(dto.getPackageId());
        if (pkg == null) {
            throw new BusinessException("快件不存在");
        }
        if (pkg.getStatus() == PackageStatus.PICKED.getCode()) {
            throw new BusinessException("已取件的快件不能登记为问题件");
        }

        ProblemPackage problem = new ProblemPackage();
        problem.setPackageId(pkg.getId());
        problem.setTrackingNo(pkg.getTrackingNo());
        problem.setProblemType(dto.getProblemType());
        problem.setProblemDesc(dto.getProblemDesc());
        problem.setHandleStatus(HandleStatus.PENDING.getCode());
        problemMapper.insert(problem);

        Integer before = pkg.getStatus();
        pkg.setStatus(PackageStatus.PROBLEM.getCode());
        packageMapper.updateById(pkg);
        saveLog(pkg, ActionType.PROBLEM, before, PackageStatus.PROBLEM.getCode(),
                "登记问题件：" + ProblemType.descOf(dto.getProblemType()));
    }

    /**
     * 分页查询问题件
     */
    public IPage<ProblemVO> page(int page, int size, String keyword, String problemType, Integer handleStatus) {
        LambdaQueryWrapper<ProblemPackage> wrapper = new LambdaQueryWrapper<ProblemPackage>()
                .orderByDesc(ProblemPackage::getCreateTime);
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(ProblemPackage::getTrackingNo, k)
                    .or().like(ProblemPackage::getProblemDesc, k));
        }
        if (StrUtil.isNotBlank(problemType)) {
            wrapper.eq(ProblemPackage::getProblemType, problemType);
        }
        if (handleStatus != null) {
            wrapper.eq(ProblemPackage::getHandleStatus, handleStatus);
        }
        IPage<ProblemPackage> pageResult = problemMapper.selectPage(new Page<>(page, size), wrapper);
        return pageResult.convert(this::toVO);
    }

    /**
     * 处理问题件
     */
    public void handle(Long id, ProblemHandleDTO dto) {
        ProblemPackage problem = problemMapper.selectById(id);
        if (problem == null) {
            throw new BusinessException("问题件不存在");
        }
        problem.setHandleStatus(dto.getHandleStatus());
        problem.setHandleResult(dto.getHandleResult());
        problem.setHandlerId(SecurityUtils.getLoginUserId());
        if (dto.getHandleStatus() == HandleStatus.DONE.getCode()) {
            problem.setHandleTime(LocalDateTime.now());
        }
        problemMapper.updateById(problem);

        if (Boolean.TRUE.equals(dto.getReturned())) {
            Package pkg = packageMapper.selectById(problem.getPackageId());
            if (pkg != null) {
                Integer before = pkg.getStatus();
                pkg.setStatus(PackageStatus.RETURNED.getCode());
                packageMapper.updateById(pkg);
                saveLog(pkg, ActionType.RETURN, before, PackageStatus.RETURNED.getCode(), "问题件处理退回");
            }
        }
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

    private ProblemVO toVO(ProblemPackage problem) {
        ProblemVO vo = new ProblemVO();
        vo.setId(problem.getId());
        vo.setPackageId(problem.getPackageId());
        vo.setTrackingNo(problem.getTrackingNo());
        vo.setProblemType(problem.getProblemType());
        vo.setProblemTypeDesc(ProblemType.descOf(problem.getProblemType()));
        vo.setProblemDesc(problem.getProblemDesc());
        vo.setHandleStatus(problem.getHandleStatus());
        vo.setHandleStatusDesc(HandleStatus.descOf(problem.getHandleStatus()));
        vo.setHandleResult(problem.getHandleResult());
        vo.setHandleTime(problem.getHandleTime());
        vo.setCreateTime(problem.getCreateTime());
        if (problem.getHandlerId() != null) {
            SysUser handler = sysUserMapper.selectById(problem.getHandlerId());
            vo.setHandlerName(handler == null ? null : handler.getRealName());
        }
        Package pkg = packageMapper.selectById(problem.getPackageId());
        if (pkg != null) {
            vo.setReceiverName(pkg.getReceiverName());
            vo.setReceiverPhone(pkg.getReceiverPhone());
            vo.setPickupCode(pkg.getPickupCode());
            vo.setShelfNo(pkg.getShelfNo());
            if (pkg.getCompanyId() != null) {
                ExpressCompany company = companyMapper.selectById(pkg.getCompanyId());
                vo.setCompanyName(company == null ? null : company.getCompanyName());
            }
        }
        return vo;
    }
}
