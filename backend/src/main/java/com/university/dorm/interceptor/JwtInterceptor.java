package com.university.dorm.interceptor;

import com.university.dorm.entity.User;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/interceptor/JwtInterceptor.java
 * 作用：拦截请求，验证 Token 并设置用户上下文
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    /**
     * 请求前处理：验证 Token
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return true-继续执行，false-中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求路径
        String path = request.getRequestURI();
        log.debug("拦截请求: {}", path);

        // 排除登录和文档路径
        if (isExcludedPath(path)) {
            return true;
        }

        // 获取 Token
        String token = extractToken(request);
        if (token == null) {
            log.warn("请求 {} 缺少 Token", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("请求 {} Token 无效", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 检查 Token 是否过期
        if (jwtUtil.isTokenExpired(token)) {
            log.warn("请求 {} Token 已过期", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 获取用户信息并存入请求属性
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            User user = userMapper.selectById(userId);
            if (user == null || user.getStatus() == 0) {
                log.warn("用户不存在或已被禁用: {}", userId);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 将用户信息存入请求属性，供后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("userRole", role);
            request.setAttribute("user", user);

            log.debug("用户 {} 认证成功，角色: {}", userId, role);
            return true;

        } catch (Exception e) {
            log.error("解析 Token 异常: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    /**
     * 从请求头中提取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(jwtUtil.getHeader());
        if (header != null && header.startsWith(jwtUtil.getPrefix())) {
            return header.substring(jwtUtil.getPrefix().length()).trim();
        }
        return null;
    }

    /**
     * 判断路径是否排除（不需要 Token）
     *
     * @param path 请求路径
     * @return true-排除，false-不排除
     */
    private boolean isExcludedPath(String path) {
        return path.contains("/auth/login")
                || path.contains("/auth/register")
                || path.contains("/doc.html")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.contains("/webjars")
                || path.contains("/static")
                || path.contains("/css")
                || path.contains("/js")
                || path.contains("/images")
                || path.contains("/favicon.ico");
    }
}