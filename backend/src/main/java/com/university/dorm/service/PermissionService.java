package com.university.dorm.service;

import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.ManagerPermission;

import java.util.List;

/**
 * 权限服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/PermissionService.java
 * 作用：定义宿舍管理员权限相关的业务方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface PermissionService {

    // ==================== 权限分配 ====================

    /**
     * 分配宿舍管理员权限
     *
     * @param managerId      管理员用户ID
     * @param dormId         宿舍ID
     * @param permissionType 权限类型（full-完全控制，readonly-只读）
     */
    void assignPermission(Long managerId, Long dormId, String permissionType);

    /**
     * 批量分配权限
     *
     * @param managerId      管理员用户ID
     * @param dormIds        宿舍ID列表
     * @param permissionType 权限类型
     */
    void batchAssignPermissions(Long managerId, List<Long> dormIds, String permissionType);

    /**
     * 移除管理员的某个宿舍权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     */
    void removePermission(Long managerId, Long dormId);

    /**
     * 移除管理员的所有权限
     *
     * @param managerId 管理员用户ID
     */
    void removeAllPermissions(Long managerId);

    /**
     * 移除某个宿舍的所有管理员权限
     *
     * @param dormId 宿舍ID
     */
    void removePermissionsByDormId(Long dormId);

    // ==================== 权限查询 ====================

    /**
     * 查询管理员管辖的所有宿舍ID
     *
     * @param managerId 管理员用户ID
     * @return 宿舍ID列表
     */
    List<Long> getManagedDormIds(Long managerId);

    /**
     * 查询管理员管辖的所有宿舍
     *
     * @param managerId 管理员用户ID
     * @return 宿舍列表
     */
    List<Dormitory> getManagedDorms(Long managerId);

    /**
     * 查询某个宿舍的所有管理员
     *
     * @param dormId 宿舍ID
     * @return 管理员权限列表
     */
    List<ManagerPermission> getManagersByDormId(Long dormId);

    /**
     * 查询管理员的权限记录
     *
     * @param managerId 管理员用户ID
     * @return 权限记录列表
     */
    List<ManagerPermission> getPermissionsByManagerId(Long managerId);

    // ==================== 权限校验 ====================

    /**
     * 检查管理员是否有权限操作某个宿舍
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return true-有权限，false-无权限
     */
    boolean hasPermission(Long managerId, Long dormId);

    /**
     * 检查管理员是否有完全控制权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return true-有完全控制，false-无
     */
    boolean hasFullPermission(Long managerId, Long dormId);

    /**
     * 检查管理员是否有只读权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return true-有只读权限，false-无
     */
    boolean hasReadonlyPermission(Long managerId, Long dormId);

    /**
     * 校验并获取管辖宿舍列表（无权限时抛异常）
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @throws com.university.dorm.exception.BusinessException 无权限时抛出
     */
    void checkPermission(Long managerId, Long dormId);

    /**
     * 校验管理员是否有完全控制权限（无权限时抛异常）
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @throws com.university.dorm.exception.BusinessException 无完全控制权限时抛出
     */
    void checkFullPermission(Long managerId, Long dormId);

    // ==================== 统计 ====================

    /**
     * 统计管理员管辖的宿舍数量
     *
     * @param managerId 管理员用户ID
     * @return 宿舍数量
     */
    Long getManagedDormCount(Long managerId);

    /**
     * 统计某个宿舍的管理员数量
     *
     * @param dormId 宿舍ID
     * @return 管理员数量
     */
    Long getManagerCountByDormId(Long dormId);

    /**
     * 获取所有有权限的管理员列表（按角色过滤）
     *
     * @return 管理员列表
     */
    List<Object> getAllManagers();

    // ==================== 权限初始化 ====================

    /**
     * 初始化权限数据（系统启动时调用）
     */
    void initPermissions();
}