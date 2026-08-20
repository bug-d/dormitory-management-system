package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.LoginRequest;
import com.university.dorm.dto.response.LoginResponse;
import com.university.dorm.entity.User;

import java.util.List;

public interface UserService {

    // ==================== 认证相关 ====================

    LoginResponse login(LoginRequest request);

    void logout(String token);

    User getCurrentUser();

    // ==================== 基础 CRUD ====================

    User getById(Long id);

    User getByUsername(String username);

    List<User> listAll();

    /**
     * 分页查询用户（支持排序）
     */
    Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword, String role, Integer status, String orderBy, String orderDir);

    void add(User user);

    void update(User user);

    void delete(Long id);

    List<User> getByRole(String role);

    // ==================== 密码管理 ====================

    void changePassword(Long userId, String oldPassword, String newPassword);

    String resetPassword(Long userId);

    boolean verifyPassword(Long userId, String rawPassword);

    // ==================== 状态管理 ====================

    void enable(Long id);

    void disable(Long id);

    boolean existsByUsername(String username);

    void updateLastLoginTime(Long userId);

    // ==================== 统计 ====================

    Long countAll();

    List<Object> countByRole();
}