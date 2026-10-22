package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 快件入库登记请求
 */
@Data
public class PackageInDTO {

    @NotBlank(message = "快递单号不能为空")
    private String trackingNo;

    @NotNull(message = "请选择快递公司")
    private Long companyId;

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收件人手机号不能为空")
    private String receiverPhone;

    private String shelfNo;

    private String remark;
}
