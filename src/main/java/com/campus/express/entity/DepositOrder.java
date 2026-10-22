package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 存件（寄存）订单
 */
@Data
@TableName("deposit_order")
public class DepositOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long studentId;

    private String itemName;

    private Integer quantity;

    private String depositCode;

    private String shelfNo;

    private Integer depositDays;

    private LocalDateTime expectTime;

    private String remark;

    private Integer status;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    private Long operatorId;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
