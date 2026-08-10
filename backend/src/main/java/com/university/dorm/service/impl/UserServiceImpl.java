package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.RoleConstant;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.LoginRequest;
import com.university.dorm.dto.response.LoginResponse;
import com.university.dorm.entity.User;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.service.UserService;
import com.university.dorm.util.JwtUtil;
import com.university.dorm.util.PasswordUtil;
import com.university.dorm.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户服务实现类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/impl/UserServiceImpl.java
 * 作用：用户服务接口实现
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;

    // ==================== 认证相关 ====================

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == StatusConstant.USER_DISABLED) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 4. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 5. 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());

        // 6. 构建响应
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .roleName(RoleConstant.getRoleName(user.getRole()))
                .status(user.getStatus())
                .build();
    }

    @Override
    public void logout(String token) {
        // JWT 是无状态的，这里可以添加 Token 到黑名单
        // 实际项目中可以用 Redis 存储黑名单
        log.info("用户登出");
    }

    @Override
    public User getCurrentUser() {
        return userMapper.selectById(securityUtil.getCurrentUserId());
    }

    // ==================== 基础 CRUD ====================

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .orderByAsc(User::getUsername)
        );
    }

    @Override
    public Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getRealName, keyword);
        }
        
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(User user) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名 " + user.getUsername() + " 已存在");
        }

        // 加密密码
        user.setPassword(passwordUtil.encode(user.getPassword()));
        user.setStatus(StatusConstant.USER_ENABLED);
        userMapper.insert(user);
        log.info("新增用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void update(User user) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果用户名变更，检查是否重复
        if (!existing.getUsername().equals(user.getUsername())) {
            if (userMapper.existsByUsername(user.getUsername())) {
                throw new BusinessException("用户名 " + user.getUsername() + " 已存在");
            }
        }

        // 如果密码变更，加密存储
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordUtil.encode(user.getPassword()));
        } else {
            user.setPassword(null);  // 不更新密码
        }

        userMapper.updateById(user);
        log.info("更新用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.deleteById(id);
        log.info("删除用户成功: {}", user.getUsername());
    }

    @Override
    public List<User> getByRole(String role) {
        return userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getRole, role)
                .eq(User::getStatus, StatusConstant.USER_ENABLED)
        );
    }

    // ==================== 密码管理 ====================

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 加密新密码
        user.setPassword(passwordUtil.encode(newPassword));
        userMapper.updateById(user);
        log.info("用户 {} 修改密码成功", user.getUsername());
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成随机密码
        String newPassword = passwordUtil.generateRandomPassword();
        user.setPassword(passwordUtil.encode(newPassword));
        userMapper.updateById(user);
        log.info("重置用户 {} 密码成功", user.getUsername());

        return newPassword;
    }

    @Override
    public boolean verifyPassword(Long userId, String rawPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        return passwordUtil.matches(rawPassword, user.getPassword());
    }

    // ==================== 状态管理 ====================

    @Override
    @Transactional
    public void enable(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(StatusConstant.USER_ENABLED);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(StatusConstant.USER_DISABLED);
        userMapper.updateById(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        userMapper.updateLastLoginTime(userId);
    }

    // ==================== 统计 ====================

    @Override
    public Long countAll() {
        return userMapper.selectCount(null);
    }

    @Override
    public List<Object> countByRole() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (User user : userMapper.selectList(null)) {
            counts.merge(user.getRole(), 1L, Long::sum);
        }
        return counts.entrySet().stream()
                .map(entry -> (Object) Map.of("role", entry.getKey(), "count", entry.getValue()))
                .toList();
    }
}
