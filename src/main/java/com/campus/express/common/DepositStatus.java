package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存件（寄存）订单状态
 */
@Getter
@AllArgsConstructor
public enum DepositStatus {

    WAIT(0, "待到站"),
    STORED(1, "在库"),
    TAKEN(2, "已取出"),
    CANCELED(3, "已取消");

    private final Integer code;
    private final String desc;

    public static String descOf(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (DepositStatus s : values()) {
            if (s.code.equals(code)) {
                return s.desc;
            }
        }
        return "未知";
    }
}
