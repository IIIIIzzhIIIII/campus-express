package com.campus.express.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 取件凭证方式
 */
@Getter
@AllArgsConstructor
public enum PickupType {

    PICKUP_CODE("PICKUP_CODE", "取件码"),
    PHONE_TAIL("PHONE_TAIL", "手机号后四位+单号"),
    SCAN("SCAN", "扫码枪扫单号");

    private final String code;
    private final String desc;

    public static String descOf(String code) {
        for (PickupType t : values()) {
            if (t.code.equals(code)) {
                return t.desc;
            }
        }
        return code;
    }
}
