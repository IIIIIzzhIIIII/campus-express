package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 货架库位
 */
@Data
@TableName("shelf")
public class Shelf implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String shelfNo;

    private String area;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;
}
