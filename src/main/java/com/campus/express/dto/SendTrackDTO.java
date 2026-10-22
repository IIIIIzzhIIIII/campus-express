package com.campus.express.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 驿站更新寄件物流状态请求
 */
@Data
public class SendTrackDTO {

    @NotNull(message = "缺少订单ID")
    private Long id;

    @NotNull(message = "请选择物流状态")
    private Integer status;

    /** 所在网点/城市 */
    private String location;

    /** 轨迹描述（留空按状态自动生成） */
    private String content;
}
