package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 问题件登记请求
 */
@Data
public class ProblemDTO {

    @NotNull(message = "请选择快件")
    private Long packageId;

    @NotBlank(message = "请选择问题类型")
    private String problemType;

    private String problemDesc;
}
