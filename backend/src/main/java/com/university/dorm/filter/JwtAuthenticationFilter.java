package com.university.dorm.filter;

import com.university.dorm.entity.User;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 认证过滤器
 * 路径：backend/src/main/java/com/university/dorm/filter/JwtAuthenticationFilter.java
 * 作用：拦截请求，验证 Token，并将用户信息存入 SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 跳过 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 获取 Token
        String header = request.getHeader(jwtUtil.getHeader());
        String token = null;

        if (header != null && header.startsWith(jwtUtil.getPrefix())) {
            token = header.substring(jwtUtil.getPrefix().length()).trim();
            log.debug("请求包含 JWT Token");
        }

        // 3. 如果 Token 存在且未认证
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证 Token
                if (jwtUtil.validateToken(token)) {
                    // 从 Token 获取用户信息
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);

                    log.info("Token 验证成功 - 用户ID: {}, 角色: {}", userId, role);

                    // 检查用户是否存在且启用
                    User user = userMapper.selectById(userId);
                    if (user != null && user.getStatus() == 1) {
                        // 创建权限列表
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        );

                        // ⭐ 关键：创建认证对象，用户名存入用户ID（用于 SecurityUtil 获取）
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                String.valueOf(userId),  // principal = 用户ID（字符串形式）
                                null,                    // credentials
                                authorities              // 权限
                        );

                        // 存入 SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.info("用户 {} 认证成功，角色: {}", userId, role);
                    } else {
                        log.warn("用户不存在或已被禁用: {}", userId);
                    }
                } else {
                    log.warn("Token 验证失败");
                }
            } catch (Exception e) {
                log.warn("Token 解析异常: {}", e.getMessage());
            }
        }

        // 4. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否需要跳过该过滤器
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/auth/login")
                || path.contains("/doc.html")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.contains("/webjars");
    }
}
