package com.campus.express.controller;

import com.campus.express.common.PageResult;
import com.campus.express.common.Result;
import com.campus.express.dto.SendOrderDTO;
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
 * 用户端寄件接口（需登录）
 */
@RestController
@RequestMapping("/api/student/send")
public class StudentSendController {

    @Resource
    private SendOrderService sendOrderService;

    /**
     * 下单寄件
     */
    @PostMapping
    public Result<SendOrderVO> create(@Valid @RequestBody SendOrderDTO dto) {
        return Result.ok("寄件下单成功，请将物品交给驿站", sendOrderService.create(dto));
    }

    /**
     * 我的寄件列表
     */
    @GetMapping("/page")
    public Result<PageResult<SendOrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(sendOrderService.pageByStudent(page, size, status)));
    }

    /**
     * 我的收件列表：按登录学生手机号匹配收件人手机号（别人在线寄给我的寄件订单）
     */
    @GetMapping("/receive/page")
    public Result<PageResult<SendOrderVO>> receivePage(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) Integer status) {
        return Result.ok(PageResult.of(sendOrderService.pageByReceiver(page, size, status)));
    }

    /**
     * 寄件详情（下单人本人或收件人均可查看）
     */
    @GetMapping("/{id}")
    public Result<SendOrderVO> detail(@PathVariable Long id) {
        return Result.ok(sendOrderService.detailForStudent(id));
    }

    /**
     * 物流轨迹
     */
    @GetMapping("/logs/{id}")
    public Result<List<SendOrderLog>> logs(@PathVariable Long id) {
        return Result.ok(sendOrderService.logsForStudent(id));
    }

    /**
     * 取消寄件（仅待揽收）
     */
    @PostMapping("/cancel/{id}")
    public Result<SendOrderVO> cancel(@PathVariable Long id) {
        return Result.ok("已取消寄件订单", sendOrderService.cancelByStudent(id));
    }
}
