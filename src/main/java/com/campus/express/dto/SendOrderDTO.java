package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生寄件下单请求
 */
@Data
public class SendOrderDTO {

    @NotNull(message = "请选择快递公司")
    private Long companyId;

    @NotBlank(message = "寄件人姓名不能为空")
    private String senderName;

    @NotBlank(message = "寄件人手机号不能为空")
    private String senderPhone;

    @NotBlank(message = "取件地点不能为空")
    private String senderAddress;

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收件人手机号不能为空")
    private String receiverPhone;

    @NotBlank(message = "收件地址不能为空")
    private String receiverAddress;

    @NotBlank(message = "物品名称不能为空")
    private String itemName;

    private BigDecimal weight;

    /** 期望取件时间，格式 yyyy-MM-dd HH:mm(:ss) */
    private String expectTime;

    private String remark;
}
