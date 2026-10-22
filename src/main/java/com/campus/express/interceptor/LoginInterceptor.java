package com.campus.express.interceptor;

import com.campus.express.common.RequiresRole;
import com.campus.express.common.Result;
import com.campus.express.entity.SysUser;
import com.campus.express.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 登录 + 权限拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        SysUser user = SecurityUtils.getLoginUser();
        if (user == null) {
            if (isApiRequest(request)) {
                writeJson(response, 401, "未登录或登录已过期");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        // 权限校验
        RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);
        if (requiresRole == null) {
            requiresRole = handlerMethod.getBeanType().getAnnotation(RequiresRole.class);
        }
        if (requiresRole != null) {
            List<String> allowed = Arrays.asList(requiresRole.value());
            if (!allowed.contains(user.getRoleCode())) {
                if (isApiRequest(request)) {
                    writeJson(response, 403, "无权限访问该功能");
                } else {
                    response.sendRedirect("/index");
                }
                return false;
            }
        }
        return true;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/") || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new String(
                objectMapper.writeValueAsBytes(Result.fail(code, msg)), StandardCharsets.UTF_8));
    }
}
