package com.university.dorm.util;

import com.university.dorm.constant.RoleConstant;
import com.university.dorm.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 * 路径：backend/src/main/java/com/university/dorm/util/SecurityUtil.java
 * 作用：获取当前登录用户信息
 */
@Slf4j
@Component
public class SecurityUtil {

    /**
     * 获取当前认证信息
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户ID
     */
    public Long getCurrentUserId() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                log.warn("SecurityContext 中没有认证信息");
                throw new BusinessException("用户未登录");
            }

            Object principal = authentication.getPrincipal();
            log.info("当前认证主体类型: {}", principal != null ? principal.getClass().getName() : "null");

            // principal 是 String（用户ID）
            if (principal instanceof String) {
                try {
                    Long userId = Long.parseLong((String) principal);
                    log.info("当前用户ID: {}", userId);
                    return userId;
                } catch (NumberFormatException e) {
                    log.warn("主体不是数字格式: {}", principal);
                }
            }

            // 如果 principal 是 UserDetails（备用）
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                try {
                    return Long.parseLong(username);
                } catch (NumberFormatException e) {
                    log.warn("UserDetails 用户名不是数字: {}", username);
                }
            }

            log.warn("无法从 SecurityContext 获取用户ID");
            throw new BusinessException("无法获取用户信息");
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            throw new BusinessException("用户未登录");
        }
    }

    /**
     * 获取当前用户名
     */
    public String getCurrentUsername() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户角色
     */
    public String getCurrentUserRole() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return null;
            }
            return authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .orElse(null);
        } catch (Exception e) {
            log.error("获取用户角色失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断当前用户是否为管理员
     */
    public boolean isAdmin() {
        try {
            String role = getCurrentUserRole();
            return RoleConstant.ADMIN.equals(role) || RoleConstant.MANAGER.equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否已登录
     */
    public boolean isAuthenticated() {
        try {
            Authentication authentication = getAuthentication();
            return authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal());
        } catch (Exception e) {
            return false;
        }
    }
}