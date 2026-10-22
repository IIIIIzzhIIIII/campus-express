package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 驿站揽收请求（回填运单号）
 */
@Data
public class SendCollectDTO {

    @NotBlank(message = "请填写运单号")
    private String trackingNo;

    /** 揽收网点/地点 */
    private String location;
}
