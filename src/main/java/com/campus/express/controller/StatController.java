package com.campus.express.controller;

import com.campus.express.common.Result;
import com.campus.express.service.StatService;
import com.campus.express.vo.StatOverviewVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据统计接口
 */
@RestController
@RequestMapping("/api/stat")
public class StatController {

    @Resource
    private StatService statService;

    @GetMapping("/overview")
    public Result<StatOverviewVO> overview() {
        return Result.ok(statService.overview());
    }

    @GetMapping("/daily")
    public Result<List<Map<String, Object>>> daily(@RequestParam(defaultValue = "14") int days) {
        return Result.ok(statService.dailyTrend(days));
    }

    @GetMapping("/company")
    public Result<List<Map<String, Object>>> company() {
        return Result.ok(statService.companyStat());
    }

    @GetMapping("/operator")
    public Result<List<Map<String, Object>>> operator() {
        return Result.ok(statService.operatorStat());
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        statService.exportPackages(response);
    }
}
