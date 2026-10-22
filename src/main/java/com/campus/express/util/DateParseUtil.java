package com.campus.express.util;

import cn.hutool.core.util.StrUtil;
import com.campus.express.common.BusinessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间解析工具（兼容前端 datetime-local 与手工输入）
 */
public class DateParseUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateParseUtil() {
    }

    /**
     * 解析时间字符串，空值返回 null
     */
    public static LocalDateTime parse(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        String s = text.trim().replace('T', ' ');
        if (s.length() == 16) {
            s += ":00";
        }
        try {
            return LocalDateTime.parse(s, FORMATTER);
        } catch (Exception e) {
            throw new BusinessException("时间格式不正确，应为 yyyy-MM-dd HH:mm");
        }
    }
}
