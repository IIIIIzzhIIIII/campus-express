package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.service.CompanyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 快递公司接口
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @GetMapping("/all")
    public Result<List<ExpressCompany>> all() {
        return Result.ok(companyService.listActive());
    }

    @GetMapping("/page")
    public Result<PageResult<ExpressCompany>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String keyword) {
        IPage<ExpressCompany> result = companyService.page(page, size, keyword);
        return Result.ok(PageResult.of(result));
    }

    @OperLog(module = "基础数据", operation = "新增快递公司")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @PostMapping
    public Result<Void> create(@RequestBody ExpressCompany company) {
        companyService.create(company);
        return Result.ok();
    }

    @OperLog(module = "基础数据", operation = "修改快递公司")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @PutMapping
    public Result<Void> update(@RequestBody ExpressCompany company) {
        companyService.update(company);
        return Result.ok();
    }

    @OperLog(module = "基础数据", operation = "删除快递公司")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return Result.ok();
    }
}
