package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 取件通知记录
 */
@Data
@TableName("notice_record")
public class NoticeRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;

    private String trackingNo;

    private String receiverPhone;

    private String noticeType;

    private String content;

    private Integer sendStatus;

    private LocalDateTime sendTime;
}
