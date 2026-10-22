package com.campus.express.aspect;

import cn.hutool.json.JSONUtil;
import com.campus.express.common.OperLog;
import com.campus.express.entity.SysOperLog;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.SysOperLogMapper;
import com.campus.express.util.IpUtil;
import com.campus.express.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志切面（记录到 sys_oper_log）
 */
@Slf4j
@Aspect
@Component
public class OperLogAspect {

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint pjp, OperLog operLog) throws Throwable {
        long start = System.currentTimeMillis();
        SysOperLog record = new SysOperLog();
        SysUser user = SecurityUtils.getLoginUser();
        if (user != null) {
            record.setUserId(user.getId());
            record.setUsername(user.getUsername());
        }
        record.setModule(operLog.module());
        record.setOperation(operLog.operation());
        record.setMethod(pjp.getSignature().toShortString());
        record.setParams(buildParams(pjp.getArgs()));
        record.setIp(IpUtil.getClientIp(SecurityUtils.getRequest()));

        Object result;
        try {
            result = pjp.proceed();
            record.setStatus(1);
        } catch (Throwable e) {
            record.setStatus(0);
            record.setErrorMsg(truncate(e.getMessage()));
            throw e;
        } finally {
            record.setCostTime(System.currentTimeMillis() - start);
            try {
                sysOperLogMapper.insert(record);
            } catch (Exception ex) {
                log.error("保存操作日志失败", ex);
            }
        }
        return result;
    }

    private String buildParams(Object[] args) {
        List<Object> list = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                    || arg instanceof MultipartFile) {
                continue;
            }
            list.add(arg);
        }
        try {
            return truncate(JSONUtil.toJsonStr(list));
        } catch (Exception e) {
            return "";
        }
    }

    private String truncate(String s) {
        if (s == null) {
            return null;
        }
        return s.length() > 2000 ? s.substring(0, 2000) : s;
    }
}
