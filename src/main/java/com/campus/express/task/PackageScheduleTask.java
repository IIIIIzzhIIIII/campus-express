package com.campus.express.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.express.entity.Package;
import com.campus.express.entity.PackageLog;
import com.campus.express.common.PackageStatus;
import com.campus.express.mapper.PackageLogMapper;
import com.campus.express.mapper.PackageMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 快件定时任务（滞留件自动标记）
 */
@Slf4j
@Component
public class PackageScheduleTask {

    @Resource
    private PackageMapper packageMapper;

    @Resource
    private PackageLogMapper packageLogMapper;

    @Value("${campus.stay-days:3}")
    private int stayDays;

    /**
     * 每天凌晨 1 点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void markStayPackages() {
        int count = doMarkStay();
        log.info("【定时任务】滞留件标记完成，共标记 {} 件", count);
    }

    /**
     * 执行滞留标记（供定时任务与手动触发共用）
     */
    public int doMarkStay() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(stayDays);
        List<Package> list = packageMapper.selectList(new LambdaQueryWrapper<Package>()
                .in(Package::getStatus, PackageStatus.IN.getCode(), PackageStatus.NOTICED.getCode())
                .lt(Package::getInTime, threshold));
        int count = 0;
        for (Package p : list) {
            Integer before = p.getStatus();
            p.setStatus(PackageStatus.STAY.getCode());
            packageMapper.updateById(p);

            PackageLog logEntity = new PackageLog();
            logEntity.setPackageId(p.getId());
            logEntity.setTrackingNo(p.getTrackingNo());
            logEntity.setAction("STAY");
            logEntity.setBeforeStatus(before);
            logEntity.setAfterStatus(PackageStatus.STAY.getCode());
            logEntity.setContent("超过 " + stayDays + " 天未取件，自动标记为滞留");
            logEntity.setOperatorName("系统");
            packageLogMapper.insert(logEntity);
            count++;
        }
        return count;
    }
}
