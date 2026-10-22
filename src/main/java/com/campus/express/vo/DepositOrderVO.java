package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 存件（寄存）订单展示 VO
 */
@Data
public class DepositOrderVO implements Serializable {

    private Long id;

    private String orderNo;

    private Long studentId;

    private String studentName;

    private String studentPhone;

    private String itemName;

    private Integer quantity;

    private String depositCode;

    private String shelfNo;

    private Integer depositDays;

    private LocalDateTime expectTime;

    private String remark;

    private Integer status;

    private String statusDesc;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    private String operatorName;

    private LocalDateTime createTime;
}
