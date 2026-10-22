package com.campus.express.controller;

import com.campus.express.common.Result;
import com.campus.express.dto.StudentLoginDTO;
import com.campus.express.dto.StudentProfileDTO;
import com.campus.express.dto.StudentRegisterDTO;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.service.CompanyService;
import com.campus.express.service.StudentAuthService;
import com.campus.express.vo.StudentVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端（学生）账号接口：注册 / 登录 / 退出 / 个人资料
 */
@RestController
@RequestMapping("/api/student")
public class StudentAuthController {

    @Resource
    private StudentAuthService studentAuthService;

    @Resource
    private CompanyService companyService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<StudentVO> register(@Valid @RequestBody StudentRegisterDTO dto) {
        return Result.ok("注册成功，请登录", studentAuthService.register(dto));
    }

    /**
     * 账号密码登录
     */
    @PostMapping("/login")
    public Result<StudentVO> login(@Valid @RequestBody StudentLoginDTO dto, HttpServletRequest request) {
        return Result.ok("登录成功", studentAuthService.login(dto, request));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        studentAuthService.logout(request);
        return Result.ok("已退出登录", null);
    }

    /**
     * 当前登录学生资料
     */
    @GetMapping("/profile")
    public Result<StudentVO> profile(HttpServletRequest request) {
        return Result.ok(studentAuthService.profile(request));
    }

    /**
     * 修改资料 / 修改密码
     */
    @PutMapping("/profile")
    public Result<StudentVO> updateProfile(@Valid @RequestBody StudentProfileDTO dto, HttpServletRequest request) {
        return Result.ok("保存成功", studentAuthService.updateProfile(dto, request));
    }

    /**
     * 快递公司列表（用户端下拉框，免登录）
     */
    @GetMapping("/companies")
    public Result<List<ExpressCompany>> companies() {
        return Result.ok(companyService.listActive());
    }
}
