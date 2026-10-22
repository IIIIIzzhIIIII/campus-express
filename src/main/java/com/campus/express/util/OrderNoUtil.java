package com.campus.express.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.Date;

/**
 * 业务单号工具类（寄件单号 / 存件单号）
 */
public class OrderNoUtil {

    private OrderNoUtil() {
    }

    /**
     * 生成业务单号：前缀 + 时间戳(yyyyMMddHHmmss) + 4 位随机数
     */
    public static String generate(String prefix) {
        return prefix + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4);
    }
}
