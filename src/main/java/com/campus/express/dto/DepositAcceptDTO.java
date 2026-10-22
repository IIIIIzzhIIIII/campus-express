package com.campus.express.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 驿站接收存件请求（上架）
 */
@Data
public class DepositAcceptDTO {

    @NotNull(message = "缺少存件单ID")
    private Long id;

    /** 货架号，留空自动分配 */
    private String shelfNo;
}
