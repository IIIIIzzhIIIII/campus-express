package com.campus.express.util;

import cn.hutool.core.util.RandomUtil;

/**
 * 取件码工具类
 */
public class PickupCodeUtil {

    private PickupCodeUtil() {
    }

    /**
     * 生成 N 位数字取件码（首位不为 0）
     */
    public static String generate(int length) {
        if (length < 4) {
            length = 4;
        }
        return RandomUtil.randomNumbers(length);
    }
}
