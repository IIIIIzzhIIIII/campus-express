package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生自助查询结果 VO
 * <p>把「驿站入库快件」与「别人寄给我的寄件订单」统一成一种结构返回，用 type 区分。</p>
 */
@Data
public class StudentQueryVO implements Serializable {

    /** 记录类型：PACKAGE 驿站入库快件 / SEND 别人寄给我的寄件订单 */
    public static final String TYPE_PACKAGE = "PACKAGE";
    public static final String TYPE_SEND = "SEND";

    /** 记录类型 */
    private String type;

    /** 主键：PACKAGE 为快件ID，SEND 为寄件订单ID */
    private Long id;

    /** 快递单号（寄件订单未揽收时为 null） */
    private String trackingNo;

    /** 寄件单号（仅 SEND） */
    private String orderNo;

    private Long companyId;

    private String companyName;

    /** 寄件人姓名（仅 SEND） */
    private String senderName;

    private String receiverName;

    private String receiverPhone;

    /** 物品名称（仅 SEND） */
    private String itemName;

    /** 取件码（仅 PACKAGE） */
    private String pickupCode;

    /** 货架号（仅 PACKAGE） */
    private String shelfNo;

    private Integer status;

    private String statusDesc;

    /** 时间：PACKAGE 为入库时间，SEND 为下单时间 */
    private LocalDateTime time;

    /** 取件时间（仅 PACKAGE） */
    private LocalDateTime pickupTime;
}
