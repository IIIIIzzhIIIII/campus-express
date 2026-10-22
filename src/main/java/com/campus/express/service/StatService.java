package com.campus.express.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.express.common.PackageStatus;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.entity.Package;
import com.campus.express.entity.ProblemPackage;
import com.campus.express.mapper.ExpressCompanyMapper;
import com.campus.express.mapper.PackageMapper;
import com.campus.express.mapper.ProblemPackageMapper;
import com.campus.express.vo.StatOverviewVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务
 */
@Service
public class StatService {

    @Resource
    private PackageMapper packageMapper;

    @Resource
    private ExpressCompanyMapper companyMapper;

    @Resource
    private ProblemPackageMapper problemMapper;

    /**
     * 统计概览
     */
    public StatOverviewVO overview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();

        StatOverviewVO vo = new StatOverviewVO();
        vo.setTodayIn(packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .ge(Package::getInTime, todayStart).lt(Package::getInTime, tomorrowStart)));
        vo.setTodayOut(packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .ge(Package::getPickupTime, todayStart).lt(Package::getPickupTime, tomorrowStart)));
        vo.setPendingCount(packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .eq(Package::getStatus, PackageStatus.NOTICED.getCode())));
        vo.setStayCount(packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .eq(Package::getStatus, PackageStatus.STAY.getCode())));
        vo.setProblemCount(problemMapper.selectCount(new LambdaQueryWrapper<ProblemPackage>()
                .ne(ProblemPackage::getHandleStatus, 2)));
        vo.setTotalCount(packageMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setPickedCount(packageMapper.selectCount(new LambdaQueryWrapper<Package>()
                .eq(Package::getStatus, PackageStatus.PICKED.getCode())));
        return vo;
    }

    /**
     * 每日入库/取件趋势
     */
    public List<Map<String, Object>> dailyTrend(int days) {
        LocalDateTime start = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        List<Map<String, Object>> inList = packageMapper.selectDailyIn(start);
        List<Map<String, Object>> outList = packageMapper.selectDailyOut(start);

        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        for (Map<String, Object> m : inList) {
            String date = String.valueOf(m.get("date"));
            Map<String, Object> row = new HashMap<>();
            row.put("date", date);
            row.put("inCount", m.get("count"));
            row.put("outCount", 0);
            merged.put(date, row);
        }
        for (Map<String, Object> m : outList) {
            String date = String.valueOf(m.get("date"));
            Map<String, Object> row = merged.computeIfAbsent(date, k -> {
                Map<String, Object> r = new HashMap<>();
                r.put("date", k);
                r.put("inCount", 0);
                return r;
            });
            row.put("outCount", m.get("count"));
        }
        return new ArrayList<>(merged.values());
    }

    /**
     * 快递公司占比
     */
    public List<Map<String, Object>> companyStat() {
        return packageMapper.selectCompanyStat();
    }

    /**
     * 操作员业绩
     */
    public List<Map<String, Object>> operatorStat() {
        return packageMapper.selectOperatorStat();
    }

    /**
     * 导出快件数据为 Excel
     */
    public void exportPackages(HttpServletResponse response) throws IOException {
        List<Package> list = packageMapper.selectList(new LambdaQueryWrapper<Package>()
                .orderByDesc(Package::getInTime));
        Map<Long, String> companyMap = new HashMap<>();
        for (ExpressCompany c : companyMapper.selectList(null)) {
            companyMap.put(c.getId(), c.getCompanyName());
        }

        List<List<String>> head = new ArrayList<>();
        head.add(List.of("快递单号"));
        head.add(List.of("快递公司"));
        head.add(List.of("收件人"));
        head.add(List.of("手机号"));
        head.add(List.of("取件码"));
        head.add(List.of("货架"));
        head.add(List.of("状态"));
        head.add(List.of("入库时间"));
        head.add(List.of("取件时间"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<List<Object>> rows = new ArrayList<>();
        for (Package p : list) {
            List<Object> row = new ArrayList<>();
            row.add(p.getTrackingNo());
            row.add(companyMap.getOrDefault(p.getCompanyId(), ""));
            row.add(p.getReceiverName());
            row.add(p.getReceiverPhone());
            row.add(p.getPickupCode());
            row.add(p.getShelfNo());
            row.add(PackageStatus.descOf(p.getStatus()));
            row.add(p.getInTime() == null ? "" : p.getInTime().format(fmt));
            row.add(p.getPickupTime() == null ? "" : p.getPickupTime().format(fmt));
            rows.add(row);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("快件数据导出", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(head).sheet("快件数据").doWrite(rows);
    }
}
