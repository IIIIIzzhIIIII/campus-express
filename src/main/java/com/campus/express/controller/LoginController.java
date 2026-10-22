package com.campus.express.controller;

import com.campus.express.common.Result;
import com.campus.express.dto.LoginDTO;
import com.campus.express.entity.SysUser;
import com.campus.express.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录控制器
 */
@Controller
public class LoginController {

    @Resource
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Result<SysUser> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        return Result.ok("登录成功", authService.login(dto, request));
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.ok();
    }
}
