package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 快件状态
 */
@Getter
@AllArgsConstructor
public enum PackageStatus {

    IN(0, "已入库待通知"),
    NOTICED(1, "已通知待取件"),
    PICKED(2, "已取件"),
    STAY(3, "滞留"),
    PROBLEM(4, "问题件"),
    RETURNED(5, "已退回");

    private final Integer code;
    private final String desc;

    public static String descOf(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (PackageStatus s : values()) {
            if (s.code.equals(code)) {
                return s.desc;
            }
        }
        return "未知";
    }
}
