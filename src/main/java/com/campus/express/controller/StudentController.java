package com.campus.express.controller;

import com.campus.express.common.BusinessException;
import com.campus.express.common.Result;
import com.campus.express.entity.StudentUser;
import com.campus.express.service.PackageService;
import com.campus.express.service.StudentQueryService;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.PackageVO;
import com.campus.express.vo.StudentQueryVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生自助查询接口（免登录） + 我的快递（需登录）
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Resource
    private PackageService packageService;

    @Resource
    private StudentQueryService studentQueryService;

    /**
     * 自助查询：返回驿站入库快件 + 别人寄给我的寄件订单（按 type 区分）
     */
    @GetMapping("/query")
    public Result<List<StudentQueryVO>> query(@RequestParam String keyword) {
        return Result.ok(studentQueryService.query(keyword));
    }

    /**
     * 我的快递：按登录手机号查询收件记录
     */
    @GetMapping("/packages")
    public Result<List<PackageVO>> myPackages() {
        StudentUser student = SecurityUtils.getLoginStudent();
        if (student == null) {
            throw new BusinessException(401, "请先登录学生账号");
        }
        return Result.ok(packageService.listByReceiverPhone(student.getPhone()));
    }
}
