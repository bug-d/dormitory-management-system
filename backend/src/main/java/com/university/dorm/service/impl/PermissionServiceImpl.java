package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.dorm.constant.RoleConstant;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.ManagerPermission;
import com.university.dorm.entity.User;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.ManagerPermissionMapper;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务实现类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/impl/PermissionServiceImpl.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final ManagerPermissionMapper permissionMapper;
    private final UserMapper userMapper;
    private final DormitoryMapper dormitoryMapper;

    // ==================== 权限分配 ====================

    @Override
    @Transactional
    public void assignPermission(Long managerId, Long dormId, String permissionType) {
        // 1. 检查用户是否为宿舍管理员
        User user = userMapper.selectById(managerId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!RoleConstant.MANAGER.equals(user.getRole())) {
            throw new BusinessException("该用户不是宿舍管理员");
        }

        // 2. 检查宿舍是否存在
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null) {
            throw new BusinessException("宿舍不存在");
        }

        // 3. 检查权限是否已存在
        if (permissionMapper.hasPermission(managerId, dormId)) {
            throw new BusinessException("该管理员已有此宿舍的权限");
        }

        // 4. 分配权限
        ManagerPermission permission = new ManagerPermission();
        permission.setManagerId(managerId);
        permission.setDormId(dormId);
        permission.setPermissionType(permissionType);
        permissionMapper.insert(permission);

        log.info("分配管理员 {} 权限到宿舍 {}，类型：{}", managerId, dormId, permissionType);
    }

    @Override
    @Transactional
    public void batchAssignPermissions(Long managerId, List<Long> dormIds, String permissionType) {
        // 检查用户是否为宿舍管理员
        User user = userMapper.selectById(managerId);
        if (user == null || !RoleConstant.MANAGER.equals(user.getRole())) {
            throw new BusinessException("该用户不是宿舍管理员");
        }

        // 先清除该管理员的所有权限
        permissionMapper.deleteByManagerId(managerId);

        // 批量分配
        List<ManagerPermission> permissions = dormIds.stream()
            .map(dormId -> {
                ManagerPermission p = new ManagerPermission();
                p.setManagerId(managerId);
                p.setDormId(dormId);
                p.setPermissionType(permissionType);
                return p;
            })
            .toList();

        for (ManagerPermission permission : permissions) {
            permissionMapper.insert(permission);
        }

        log.info("批量分配管理员 {} 权限到 {} 个宿舍", managerId, dormIds.size());
    }

    @Override
    @Transactional
    public void removePermission(Long managerId, Long dormId) {
        int deleted = permissionMapper.deleteByManagerIdAndDormId(managerId, dormId);
        if (deleted == 0) {
            throw new BusinessException("权限不存在");
        }
        log.info("移除管理员 {} 的宿舍 {} 权限", managerId, dormId);
    }

    @Override
    @Transactional
    public void removeAllPermissions(Long managerId) {
        int deleted = permissionMapper.deleteByManagerId(managerId);
        log.info("移除管理员 {} 的所有权限（{} 条）", managerId, deleted);
    }

    @Override
    @Transactional
    public void removePermissionsByDormId(Long dormId) {
        int deleted = permissionMapper.deleteByDormId(dormId);
        log.info("移除宿舍 {} 的所有管理员权限（{} 条）", dormId, deleted);
    }

    // ==================== 权限查询 ====================

    @Override
    public List<Long> getManagedDormIds(Long managerId) {
        return permissionMapper.selectDormIdsByManagerId(managerId);
    }

    @Override
    public List<Dormitory> getManagedDorms(Long managerId) {
        List<Long> dormIds = permissionMapper.selectDormIdsByManagerId(managerId);
        if (dormIds.isEmpty()) {
            return new ArrayList<>();
        }
        return dormitoryMapper.selectBatchIds(dormIds);
    }

    @Override
    public List<ManagerPermission> getManagersByDormId(Long dormId) {
        return permissionMapper.selectByDormId(dormId);
    }

    @Override
    public List<ManagerPermission> getPermissionsByManagerId(Long managerId) {
        return permissionMapper.selectByManagerId(managerId);
    }

    // ==================== 权限校验 ====================

    @Override
    public boolean hasPermission(Long managerId, Long dormId) {
        return permissionMapper.hasPermission(managerId, dormId);
    }

    @Override
    public boolean hasFullPermission(Long managerId, Long dormId) {
        return permissionMapper.hasFullPermission(managerId, dormId);
    }

    @Override
    public boolean hasReadonlyPermission(Long managerId, Long dormId) {
        // 检查是否存在只读权限
        ManagerPermission permission = permissionMapper.selectOne(
            new LambdaQueryWrapper<ManagerPermission>()
                .eq(ManagerPermission::getManagerId, managerId)
                .eq(ManagerPermission::getDormId, dormId)
                .eq(ManagerPermission::getPermissionType, StatusConstant.PERMISSION_READONLY)
        );
        return permission != null;
    }

    @Override
    public void checkPermission(Long managerId, Long dormId) {
        if (!hasPermission(managerId, dormId)) {
            throw new BusinessException("您没有权限操作该宿舍");
        }
    }

    @Override
    public void checkFullPermission(Long managerId, Long dormId) {
        if (!hasFullPermission(managerId, dormId)) {
            throw new BusinessException("您没有完全控制权限，只能查看");
        }
    }

    // ==================== 统计 ====================

    @Override
    public Long getManagedDormCount(Long managerId) {
        return permissionMapper.countByManagerId(managerId);
    }

    @Override
    public Long getManagerCountByDormId(Long dormId) {
        return permissionMapper.countByDormId(dormId);
    }

    @Override
    public List<Object> getAllManagers() {
        // 查询所有宿舍管理员用户
        return new ArrayList<>(userMapper.selectList(
            new LambdaQueryWrapper<User>()
                .eq(User::getRole, RoleConstant.MANAGER)
                .eq(User::getStatus, StatusConstant.USER_ENABLED)
        ));
    }

    // ==================== 权限初始化 ====================

    @Override
    @Transactional
    public void initPermissions() {
        log.info("开始初始化权限数据...");
        // 可以在这里添加默认权限初始化逻辑
        log.info("权限数据初始化完成");
    }
}