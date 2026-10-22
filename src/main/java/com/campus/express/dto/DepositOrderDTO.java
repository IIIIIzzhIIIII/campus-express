package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学生存件（寄存）申请请求
 */
@Data
public class DepositOrderDTO {

    @NotBlank(message = "物品名称不能为空")
    private String itemName;

    private Integer quantity;

    private Integer depositDays;

    /** 预计送达驿站时间，格式 yyyy-MM-dd HH:mm(:ss) */
    private String expectTime;

    private String remark;
}
