package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取件办理请求
 */
@Data
public class PickupDTO {

    /** 取件凭证方式：PICKUP_CODE / PHONE_TAIL / SCAN */
    @NotBlank(message = "请选择取件凭证方式")
    private String type;

    /** 取件码 */
    private String pickupCode;

    /** 手机号（后四位校验时传完整手机号或后四位） */
    private String phone;

    /** 快递单号 */
    private String trackingNo;
}
