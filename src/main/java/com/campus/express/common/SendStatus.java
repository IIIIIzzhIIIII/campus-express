package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 寄件订单状态
 */
@Getter
@AllArgsConstructor
public enum SendStatus {

    WAIT_COLLECT(0, "待揽收"),
    COLLECTED(1, "已揽收"),
    IN_TRANSIT(2, "运输中"),
    DELIVERING(3, "派送中"),
    SIGNED(4, "已签收"),
    CANCELED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static String descOf(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (SendStatus s : values()) {
            if (s.code.equals(code)) {
                return s.desc;
            }
        }
        return "未知";
    }

    /** 是否允许取消（仅待揽收可取消） */
    public static boolean cancelable(Integer code) {
        return WAIT_COLLECT.getCode().equals(code);
    }
}
