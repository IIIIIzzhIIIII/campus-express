package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问题件类型
 */
@Getter
@AllArgsConstructor
public enum ProblemType {

    STAY("STAY", "滞留"),
    WRONG("WRONG", "错件"),
    DAMAGE("DAMAGE", "破损"),
    LOST("LOST", "丢件"),
    REJECT("REJECT", "拒收"),
    RETURN("RETURN", "退回"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String desc;

    public static String descOf(String code) {
        for (ProblemType t : values()) {
            if (t.code.equals(code)) {
                return t.desc;
            }
        }
        return code;
    }
}
