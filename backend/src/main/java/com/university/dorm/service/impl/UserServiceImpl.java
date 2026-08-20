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
import com.university.dorm.service.OperationLogService;
import com.university.dorm.service.UserService;
import com.university.dorm.util.JwtUtil;
import com.university.dorm.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OperationLogService operationLogService;

    // ==================== 认证相关 ====================

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == StatusConstant.USER_DISABLED) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        userMapper.updateLastLoginTime(user.getId());

        // 记录登录日志
        operationLogService.saveLog(
                user.getId(),
                user.getUsername(),
                "LOGIN",
                "USER",
                user.getId(),
                user.getRealName() + " 登录系统"
        );

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
        log.info("用户登出");
    }

    @Override
    public User getCurrentUser() {
        return null;
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
    public Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword, String role, Integer status, String orderBy, String orderDir) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword);
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        // 排序
        if (orderBy != null && !orderBy.isEmpty()) {
            boolean isAsc = "asc".equalsIgnoreCase(orderDir);
            switch (orderBy) {
                case "id":
                    wrapper.orderBy(true, isAsc, User::getId);
                    break;
                case "username":
                    wrapper.orderBy(true, isAsc, User::getUsername);
                    break;
                case "realName":
                    wrapper.orderBy(true, isAsc, User::getRealName);
                    break;
                case "role":
                    wrapper.orderBy(true, isAsc, User::getRole);
                    break;
                case "lastLoginTime":
                    wrapper.orderBy(true, isAsc, User::getLastLoginTime);
                    break;
                default:
                    wrapper.orderBy(true, isAsc, User::getId);
                    break;
            }
        } else {
            wrapper.orderByAsc(User::getId);
        }

        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(User user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名 " + user.getUsername() + " 已存在");
        }

        // 使用初始密码
        user.setPassword(passwordUtil.encode(passwordUtil.getInitPassword()));
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

        if (!existing.getUsername().equals(user.getUsername())) {
            if (userMapper.existsByUsername(user.getUsername())) {
                throw new BusinessException("用户名 " + user.getUsername() + " 已存在");
            }
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordUtil.encode(user.getPassword()));
        } else {
            user.setPassword(null);
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

        if (!passwordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

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

        // ⭐ 重置为固定默认密码（从配置文件读取）
        String defaultPassword = passwordUtil.getResetPassword();
        user.setPassword(passwordUtil.encode(defaultPassword));
        userMapper.updateById(user);
        log.info("重置用户 {} 密码为默认密码", user.getUsername());

        // 返回默认密码，前端显示"密码已重置为 123456"
        return defaultPassword;
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
        log.info("启用用户成功: {}", user.getUsername());
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
        log.info("禁用用户成功: {}", user.getUsername());
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
        return null;
    }
}