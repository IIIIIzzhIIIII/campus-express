package com.campus.express.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生用户展示 VO（不含密码）
 */
@Data
public class StudentVO implements Serializable {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private Integer status;

    private LocalDateTime createTime;
}
