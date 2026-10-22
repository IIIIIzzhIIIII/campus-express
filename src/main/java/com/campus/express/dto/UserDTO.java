package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户新增/编辑请求
 */
@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "账号不能为空")
    private String username;

    /** 编辑时可为空（不修改密码） */
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String phone;

    @NotBlank(message = "请选择角色")
    private String roleCode;

    private Integer status;

    private String remark;
}
