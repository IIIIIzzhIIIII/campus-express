package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.entity.Shelf;
import com.campus.express.service.ShelfService;
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
 * 货架接口
 */
@RestController
@RequestMapping("/api/shelf")
public class ShelfController {

    @Resource
    private ShelfService shelfService;

    @GetMapping("/all")
    public Result<List<Shelf>> all() {
        return Result.ok(shelfService.listActive());
    }

    @GetMapping("/page")
    public Result<PageResult<Shelf>> page(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String keyword) {
        IPage<Shelf> result = shelfService.page(page, size, keyword);
        return Result.ok(PageResult.of(result));
    }

    @OperLog(module = "基础数据", operation = "新增货架")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @PostMapping
    public Result<Void> create(@RequestBody Shelf shelf) {
        shelfService.create(shelf);
        return Result.ok();
    }

    @OperLog(module = "基础数据", operation = "修改货架")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @PutMapping
    public Result<Void> update(@RequestBody Shelf shelf) {
        shelfService.update(shelf);
        return Result.ok();
    }

    @OperLog(module = "基础数据", operation = "删除货架")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        shelfService.delete(id);
        return Result.ok();
    }
}
