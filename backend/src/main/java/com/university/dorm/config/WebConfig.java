package com.university.dorm.config;

import com.university.dorm.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/config/WebConfig.java
 * 作用：配置跨域、拦截器、静态资源等
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 跨域配置
     * 允许前端跨域访问后端 API
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许跨域的来源（前端地址）
                .allowedOrigins(
                        "http://localhost:5173",      // Vue 开发环境
                        "http://localhost:3000",      // React 开发环境
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:3000"
                )
                // 允许跨域的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许跨域的请求头
                .allowedHeaders("*")
                // 是否允许携带凭证（Cookie）
                .allowCredentials(true)
                // 预检请求缓存时间（单位：秒）
                .maxAge(3600);
    }

    /**
     * 拦截器配置
     * 添加 JWT 拦截器进行 Token 验证
     *
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要 Token 验证的路径
                .excludePathPatterns(
                        // 登录接口
                        "/auth/login",
                        // 注册接口（如有）
                        "/auth/register",
                        // 静态资源
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        // API 文档
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );
    }
}