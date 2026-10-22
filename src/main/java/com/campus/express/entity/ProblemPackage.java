package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题件
 */
@Data
@TableName("problem_package")
public class ProblemPackage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;

    private String trackingNo;

    private String problemType;

    private String problemDesc;

    private Integer handleStatus;

    private String handleResult;

    private Long handlerId;

    private LocalDateTime handleTime;

    private LocalDateTime createTime;
}
