package com.campus.express.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 学生资料修改请求
 */
@Data
public class StudentProfileDTO {

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "请输入 11 位手机号")
    private String phone;

    /** 旧密码（修改密码时必填） */
    private String oldPassword;

    /** 新密码（不修改密码时留空） */
    private String newPassword;
}
