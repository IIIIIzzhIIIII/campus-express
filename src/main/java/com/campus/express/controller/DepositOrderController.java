package com.campus.express.controller;

import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.Result;
import com.campus.express.dto.DepositAcceptDTO;
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
 * 存件管理接口（后台：接收上架与取出办理）
 */
@RestController
@RequestMapping("/api/deposit")
public class DepositOrderController {

    @Resource
    private DepositOrderService depositOrderService;

    @GetMapping("/page")
    public Result<PageResult<DepositOrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(depositOrderService.page(page, size, keyword, status)));
    }

    @GetMapping("/detail/{id}")
    public Result<DepositOrderVO> detail(@PathVariable Long id) {
        return Result.ok(depositOrderService.detail(id));
    }

    @OperLog(module = "存件管理", operation = "接收存件上架")
    @PostMapping("/accept")
    public Result<DepositOrderVO> accept(@Valid @RequestBody DepositAcceptDTO dto) {
        return Result.ok("已接收并存件上架", depositOrderService.accept(dto));
    }

    @OperLog(module = "存件管理", operation = "办理存件取出")
    @PostMapping("/out/{id}")
    public Result<DepositOrderVO> takeOut(@PathVariable Long id) {
        return Result.ok("已办理取出", depositOrderService.takeOut(id));
    }
}
