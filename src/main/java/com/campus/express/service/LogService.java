package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.entity.SysLoginLog;
import com.campus.express.entity.SysOperLog;
import com.campus.express.mapper.SysLoginLogMapper;
import com.campus.express.mapper.SysOperLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 日志服务
 */
@Service
public class LogService {

    @Resource
    private SysOperLogMapper operLogMapper;

    @Resource
    private SysLoginLogMapper loginLogMapper;

    public IPage<SysOperLog> operLogPage(int page, int size, String keyword) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<SysOperLog>()
                .orderByDesc(SysOperLog::getCreateTime);
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(SysOperLog::getUsername, k)
                    .or().like(SysOperLog::getOperation, k)
                    .or().like(SysOperLog::getModule, k));
        }
        return operLogMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public IPage<SysLoginLog> loginLogPage(int page, int size, String keyword) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<SysLoginLog>()
                .orderByDesc(SysLoginLog::getLoginTime);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(SysLoginLog::getUsername, keyword.trim());
        }
        return loginLogMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
