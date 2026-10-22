package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.dto.UserDTO;
import com.campus.express.entity.SysUser;
import com.campus.express.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口（仅超级管理员）
 */
@RequiresRole(RoleCode.SUPER_ADMIN)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String keyword) {
        IPage<SysUser> result = userService.page(page, size, keyword);
        return Result.ok(PageResult.of(result));
    }

    @OperLog(module = "用户管理", operation = "新增用户")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        userService.create(dto);
        return Result.ok();
    }

    @OperLog(module = "用户管理", operation = "修改用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        userService.update(dto);
        return Result.ok();
    }

    @OperLog(module = "用户管理", operation = "重置密码")
    @PostMapping("/reset/{id}")
    public Result<Void> reset(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.<Void>ok("密码已重置为 123456", null);
    }

    @OperLog(module = "用户管理", operation = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }
}
