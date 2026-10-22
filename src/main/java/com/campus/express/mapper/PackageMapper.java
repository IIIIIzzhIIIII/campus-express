package com.campus.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.express.entity.Package;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PackageMapper extends BaseMapper<Package> {

    // 说明：以下 SQL 同时兼容 MySQL 8 与 H2（单机版 / exe），因此：
    // 1. DATE_FORMAT 改为标准 CAST(... AS DATE)，IFNULL 改为标准 COALESCE；
    // 2. `date` / `count` / `value` / `total` 在 H2 / MySQL 中可能是关键字，统一用反引号包裹别名。

    /** 按天统计入库数量 */
    @Select("SELECT CAST(in_time AS DATE) AS `date`, COUNT(*) AS `count` " +
            "FROM `package` WHERE deleted = 0 AND in_time >= #{start} " +
            "GROUP BY CAST(in_time AS DATE) ORDER BY CAST(in_time AS DATE)")
    List<Map<String, Object>> selectDailyIn(@Param("start") LocalDateTime start);

    /** 按天统计取件数量 */
    @Select("SELECT CAST(pickup_time AS DATE) AS `date`, COUNT(*) AS `count` " +
            "FROM `package` WHERE deleted = 0 AND pickup_time >= #{start} " +
            "GROUP BY CAST(pickup_time AS DATE) ORDER BY CAST(pickup_time AS DATE)")
    List<Map<String, Object>> selectDailyOut(@Param("start") LocalDateTime start);

    /** 按快递公司统计快件数量 */
    @Select("SELECT COALESCE(c.company_name, '未知') AS `name`, COUNT(p.id) AS `value` " +
            "FROM `package` p LEFT JOIN express_company c ON p.company_id = c.id " +
            "WHERE p.deleted = 0 GROUP BY COALESCE(c.company_name, '未知') ORDER BY `value` DESC")
    List<Map<String, Object>> selectCompanyStat();

    /** 按操作员统计入库/取件数量 */
    @Select("SELECT COALESCE(u.real_name, '未知') AS `name`, " +
            "SUM(CASE WHEN p.status IN (0,1,2,3,4,5) THEN 1 ELSE 0 END) AS `total`, " +
            "SUM(CASE WHEN p.pickup_time IS NOT NULL THEN 1 ELSE 0 END) AS `picked` " +
            "FROM `package` p LEFT JOIN sys_user u ON p.operator_id = u.id " +
            "WHERE p.deleted = 0 GROUP BY COALESCE(u.real_name, '未知') ORDER BY `total` DESC")
    List<Map<String, Object>> selectOperatorStat();
}
