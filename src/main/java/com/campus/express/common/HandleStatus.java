package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问题件处理状态
 */
@Getter
@AllArgsConstructor
public enum HandleStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    DONE(2, "已处理");

    private final Integer code;
    private final String desc;

    public static String descOf(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (HandleStatus s : values()) {
            if (s.code.equals(code)) {
                return s.desc;
            }
        }
        return "未知";
    }
}
