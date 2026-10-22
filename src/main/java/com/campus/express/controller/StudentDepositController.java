package com.campus.express.controller;

import com.campus.express.common.PageResult;
import com.campus.express.common.Result;
import com.campus.express.dto.DepositOrderDTO;
import com.campus.express.service.DepositOrderService;
import com.campus.express.vo.DepositOrderVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端存件（寄存）接口（需登录）
 */
@RestController
@RequestMapping("/api/student/deposit")
public class StudentDepositController {

    @Resource
    private DepositOrderService depositOrderService;

    /**
     * 提交存件申请
     */
    @PostMapping
    public Result<DepositOrderVO> create(@Valid @RequestBody DepositOrderDTO dto) {
        return Result.ok("存件申请已提交，请凭存件码到驿站办理", depositOrderService.create(dto));
    }

    /**
     * 我的存件列表
     */
    @GetMapping("/page")
    public Result<PageResult<DepositOrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(depositOrderService.pageByStudent(page, size, status)));
    }

    /**
     * 存件详情
     */
    @GetMapping("/{id}")
    public Result<DepositOrderVO> detail(@PathVariable Long id) {
        return Result.ok(depositOrderService.detailForStudent(id));
    }

    /**
     * 取消存件申请（仅待到站）
     */
    @PostMapping("/cancel/{id}")
    public Result<DepositOrderVO> cancel(@PathVariable Long id) {
        return Result.ok("已取消存件申请", depositOrderService.cancelByStudent(id));
    }
}
