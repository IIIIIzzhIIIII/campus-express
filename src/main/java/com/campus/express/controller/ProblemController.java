package com.campus.express.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.express.common.OperLog;
import com.campus.express.common.PageResult;
import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.common.RoleCode;
import com.campus.express.dto.ProblemDTO;
import com.campus.express.dto.ProblemHandleDTO;
import com.campus.express.service.ProblemService;
import com.campus.express.vo.ProblemVO;
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
 * 问题件接口
 */
@RestController
@RequestMapping("/api/problem")
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @GetMapping("/page")
    public Result<PageResult<ProblemVO>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String problemType,
                                              @RequestParam(required = false) Integer handleStatus) {
        IPage<ProblemVO> result = problemService.page(page, size, keyword, problemType, handleStatus);
        return Result.ok(PageResult.of(result));
    }

    @OperLog(module = "问题件管理", operation = "登记问题件")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody ProblemDTO dto) {
        problemService.register(dto);
        return Result.ok();
    }

    @OperLog(module = "问题件管理", operation = "处理问题件")
    @RequiresRole(RoleCode.SUPER_ADMIN)
    @PostMapping("/handle/{id}")
    public Result<Void> handle(@PathVariable Long id, @Valid @RequestBody ProblemHandleDTO dto) {
        problemService.handle(id, dto);
        return Result.ok();
    }
}
