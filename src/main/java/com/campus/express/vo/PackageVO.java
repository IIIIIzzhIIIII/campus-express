package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 快件展示 VO
 */
@Data
public class PackageVO implements Serializable {

    private Long id;

    private String trackingNo;

    private Long companyId;

    private String companyName;

    private String receiverName;

    private String receiverPhone;

    private String pickupCode;

    private String shelfNo;

    private Integer status;

    private String statusDesc;

    private LocalDateTime inTime;

    private LocalDateTime noticeTime;

    private Integer noticeCount;

    private LocalDateTime pickupTime;

    private String pickupType;

    private String pickupTypeDesc;

    private String operatorName;

    private String remark;
}
