package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 快件操作日志
 */
@Data
@TableName("package_log")
public class PackageLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;

    private String trackingNo;

    private String action;

    private Integer beforeStatus;

    private Integer afterStatus;

    private String content;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime createTime;
}
