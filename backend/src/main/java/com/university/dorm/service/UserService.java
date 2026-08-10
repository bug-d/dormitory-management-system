package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.LoginRequest;
import com.university.dorm.dto.response.LoginResponse;
import com.university.dorm.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/UserService.java
 * 作用：定义用户相关的业务方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface UserService {

    // ==================== 认证相关 ====================

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（Token + 用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     *
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户
     */
    User getCurrentUser();

    // ==================== 基础 CRUD ====================

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User getById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User getByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> listAll();

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字
     * @return 分页结果
     */
    Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 新增用户
     *
     * @param user 用户对象
     */
    void add(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     */
    void update(User user);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 根据角色查询用户列表
     *
     * @param role 角色
     * @return 用户列表
     */
    List<User> getByRole(String role);

    // ==================== 密码管理 ====================

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码（管理员操作）
     *
     * @param userId 用户ID
     * @return 新密码（明文）
     */
    String resetPassword(Long userId);

    /**
     * 验证密码是否正确
     *
     * @param userId         用户ID
     * @param rawPassword    明文密码
     * @return true-正确，false-错误
     */
    boolean verifyPassword(Long userId, String rawPassword);

    // ==================== 状态管理 ====================

    /**
     * 启用用户
     *
     * @param id 用户ID
     */
    void enable(Long id);

    /**
     * 禁用用户
     *
     * @param id 用户ID
     */
    void disable(Long id);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);

    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);

    // ==================== 统计 ====================

    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    Long countAll();

    /**
     * 统计各角色用户数量
     *
     * @return 统计数据
     */
    List<Object> countByRole();
}