package com.campus.express.config;

import com.campus.express.interceptor.LoginInterceptor;
import com.campus.express.interceptor.StudentInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置（注册登录/权限拦截器）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private StudentInterceptor studentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/api/login", "/api/logout",
                        "/student/**", "/api/student/**",
                        "/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**",
                        "/favicon.ico", "/error",
                        "/css/**", "/js/**", "/lib/**", "/images/**"
                );

        // 用户端（学生）：仅拦截需要登录的页面与接口
        registry.addInterceptor(studentInterceptor)
                .addPathPatterns(
                        "/student/center", "/student/center/**",
                        "/api/student/profile", "/api/student/profile/**",
                        "/api/student/packages",
                        "/api/student/send", "/api/student/send/**",
                        "/api/student/deposit", "/api/student/deposit/**"
                )
                .excludePathPatterns(
                        "/api/student/query", "/api/student/companies",
                        "/api/student/login", "/api/student/register", "/api/student/logout"
                );
    }
}

