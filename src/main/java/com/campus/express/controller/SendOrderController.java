package com.campus.express.controller;

import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.Result;
import com.campus.express.dto.SendCollectDTO;
import com.campus.express.dto.SendTrackDTO;
import com.campus.express.entity.SendOrderLog;
import com.campus.express.service.SendOrderService;
import com.campus.express.vo.SendOrderVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 寄件订单管理接口（后台：驿站揽收与物流跟踪）
 */
@RestController
@RequestMapping("/api/send")
public class SendOrderController {

    @Resource
    private SendOrderService sendOrderService;

    @GetMapping("/page")
    public Result<PageResult<SendOrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(sendOrderService.page(page, size, keyword, status)));
    }

    @GetMapping("/detail/{id}")
    public Result<SendOrderVO> detail(@PathVariable Long id) {
        return Result.ok(sendOrderService.detail(id));
    }

    @GetMapping("/logs/{id}")
    public Result<List<SendOrderLog>> logs(@PathVariable Long id) {
        return Result.ok(sendOrderService.logs(id));
    }

    @OperLog(module = "寄件管理", operation = "驿站揽收寄件")
    @PostMapping("/collect/{id}")
    public Result<SendOrderVO> collect(@PathVariable Long id, @Valid @RequestBody SendCollectDTO dto) {
        return Result.ok("揽收成功", sendOrderService.collect(id, dto));
    }

    @OperLog(module = "寄件管理", operation = "更新寄件物流")
    @PostMapping("/status")
    public Result<SendOrderVO> updateStatus(@Valid @RequestBody SendTrackDTO dto) {
        return Result.ok("物流状态已更新", sendOrderService.updateStatus(dto));
    }

    @OperLog(module = "寄件管理", operation = "取消寄件订单")
    @PostMapping("/cancel/{id}")
    public Result<SendOrderVO> cancel(@PathVariable Long id) {
        return Result.ok("已取消寄件订单", sendOrderService.cancel(id));
    }
}
