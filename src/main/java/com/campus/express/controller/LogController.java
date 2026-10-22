package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.entity.SysLoginLog;
import com.campus.express.entity.SysOperLog;
import com.campus.express.service.LogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志接口（仅超级管理员）
 */
@RequiresRole(RoleCode.SUPER_ADMIN)
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Resource
    private LogService logService;

    @GetMapping("/oper")
    public Result<PageResult<SysOperLog>> oper(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String keyword) {
        IPage<SysOperLog> result = logService.operLogPage(page, size, keyword);
        return Result.ok(PageResult.of(result));
    }

    @GetMapping("/login")
    public Result<PageResult<SysLoginLog>> login(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String keyword) {
        IPage<SysLoginLog> result = logService.loginLogPage(page, size, keyword);
        return Result.ok(PageResult.of(result));
    }
}
