package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 快件主表
 */
@Data
@TableName("`package`")
public class Package implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String trackingNo;

    private Long companyId;

    private String receiverName;

    private String receiverPhone;

    private String pickupCode;

    private String shelfNo;

    private Integer status;

    private LocalDateTime inTime;

    private LocalDateTime noticeTime;

    private Integer noticeCount;

    private LocalDateTime pickupTime;

    private String pickupType;

    private Long operatorId;

    private String remark;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
