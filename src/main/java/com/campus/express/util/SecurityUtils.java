package com.campus.express.util;

import com.campus.express.entity.StudentUser;
import com.campus.express.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录用户工具类
 */
public class SecurityUtils {

    public static final String SESSION_USER = "loginUser";

    /** 用户端（学生）登录会话键 */
    public static final String SESSION_STUDENT = "loginStudent";

    private SecurityUtils() {
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    public static SysUser getLoginUser() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (SysUser) session.getAttribute(SESSION_USER);
    }

    public static Long getLoginUserId() {
        SysUser user = getLoginUser();
        return user == null ? null : user.getId();
    }

    /**
     * 当前登录的学生用户（用户端）
     */
    public static StudentUser getLoginStudent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (StudentUser) session.getAttribute(SESSION_STUDENT);
    }
}

