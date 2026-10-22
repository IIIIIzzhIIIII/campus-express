package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据统计概览 VO
 */
@Data
public class StatOverviewVO implements Serializable {

    /** 今日入库 */
    private Long todayIn;

    /** 今日取件 */
    private Long todayOut;

    /** 待取件（已通知未取） */
    private Long pendingCount;

    /** 滞留件 */
    private Long stayCount;

    /** 问题件 */
    private Long problemCount;

    /** 累计入库 */
    private Long totalCount;

    /** 累计取件 */
    private Long pickedCount;
}
