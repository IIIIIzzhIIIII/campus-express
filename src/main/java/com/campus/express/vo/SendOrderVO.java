package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 寄件订单展示 VO
 */
@Data
public class SendOrderVO implements Serializable {

    private Long id;

    private String orderNo;

    private Long studentId;

    private String studentName;

    private String studentPhone;

    private Long companyId;

    private String companyName;

    private String trackingNo;

    private String senderName;

    private String senderPhone;

    private String senderAddress;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String itemName;

    private BigDecimal weight;

    private LocalDateTime expectTime;

    private String remark;

    private Integer status;

    private String statusDesc;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
