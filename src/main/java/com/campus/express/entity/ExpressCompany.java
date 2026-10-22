package com.campus.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 快递公司
 */
@Data
@TableName("express_company")
public class ExpressCompany implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String companyCode;

    private String companyName;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;
}
