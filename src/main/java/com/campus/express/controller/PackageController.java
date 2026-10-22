package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.dto.PackageInDTO;
import com.campus.express.dto.PickupDTO;
import com.campus.express.entity.PackageLog;
import com.campus.express.service.PackageService;
import com.campus.express.vo.PackageVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 快件接口
 */
@RestController
@RequestMapping("/api/package")
public class PackageController {

    @Resource
    private PackageService packageService;

    @OperLog(module = "快件管理", operation = "快件入库登记")
    @PostMapping("/in")
    public Result<PackageVO> inbound(@Valid @RequestBody PackageInDTO dto) {
        return Result.ok("入库成功", packageService.inbound(dto));
    }

    @GetMapping("/page")
    public Result<PageResult<PackageVO>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) Long companyId,
                                              @RequestParam(required = false) String dateStart,
                                              @RequestParam(required = false) String dateEnd) {
        IPage<PackageVO> result = packageService.page(page, size, keyword, status, companyId, dateStart, dateEnd);
        return Result.ok(PageResult.of(result));
    }

    @GetMapping("/detail/{id}")
    public Result<PackageVO> detail(@PathVariable Long id) {
        return Result.ok(packageService.detail(id));
    }

    @GetMapping("/logs/{id}")
    public Result<List<PackageLog>> logs(@PathVariable Long id) {
        return Result.ok(packageService.logs(id));
    }

    @OperLog(module = "快件管理", operation = "发送取件通知")
    @PostMapping("/notice/{id}")
    public Result<PackageVO> notice(@PathVariable Long id) {
        return Result.ok("通知发送成功", packageService.notice(id));
    }

    @OperLog(module = "快件管理", operation = "批量发送取件通知")
    @PostMapping("/notice/batch")
    public Result<Integer> noticeBatch(@RequestBody List<Long> ids) {
        return Result.ok("批量通知完成", packageService.noticeBatch(ids));
    }

    @OperLog(module = "快件管理", operation = "取件办理")
    @PostMapping("/pickup")
    public Result<PackageVO> pickup(@Valid @RequestBody PickupDTO dto) {
        return Result.ok("取件成功", packageService.pickup(dto));
    }

    @OperLog(module = "快件管理", operation = "删除快件")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        packageService.delete(id);
        return Result.ok();
    }
}
