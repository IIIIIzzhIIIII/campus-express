package com.campus.express.interceptor;

import com.campus.express.common.Result;
import com.campus.express.entity.StudentUser;
import com.campus.express.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 用户端（学生）登录拦截器
 */
@Component
public class StudentInterceptor implements HandlerInterceptor {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        StudentUser student = SecurityUtils.getLoginStudent();
        if (student != null) {
            return true;
        }

        if (request.getRequestURI().startsWith("/api/")) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new String(
                    objectMapper.writeValueAsBytes(Result.fail(401, "请先登录学生账号")), StandardCharsets.UTF_8));
        } else {
            response.sendRedirect("/student/login");
        }
        return false;
    }
}
