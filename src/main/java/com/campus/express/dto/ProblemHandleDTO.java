package com.campus.express.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 问题件处理请求
 */
@Data
public class ProblemHandleDTO {

    @NotNull(message = "请选择处理状态")
    private Integer handleStatus;

    private String handleResult;

    /** 是否同时将快件标记为已退回（状态 5） */
    private Boolean returned;
}
