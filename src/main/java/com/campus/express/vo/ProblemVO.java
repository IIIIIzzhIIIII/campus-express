package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题件展示 VO
 */
@Data
public class ProblemVO implements Serializable {

    private Long id;

    private Long packageId;

    private String trackingNo;

    private String problemType;

    private String problemTypeDesc;

    private String problemDesc;

    private Integer handleStatus;

    private String handleStatusDesc;

    private String handleResult;

    private String handlerName;

    private LocalDateTime handleTime;

    private LocalDateTime createTime;

    /** 关联快件信息 */
    private String receiverName;

    private String receiverPhone;

    private String companyName;

    private String pickupCode;

    private String shelfNo;
}
