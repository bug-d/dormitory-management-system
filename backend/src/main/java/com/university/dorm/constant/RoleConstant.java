package com.university.dorm.constant;

/**
 * 角色常量类
 * 
 * 路径：backend/src/main/java/com/university/dorm/constant/RoleConstant.java
 * 作用：定义系统中所有角色，统一管理，避免硬编码
 * 
 * @author University Dorm Team
 * @version 1.0.0
 */
public class RoleConstant {

    /**
     * 私有构造函数，防止实例化
     * 因为这是一个常量类，不需要被实例化
     */
    private RoleConstant() {
        throw new UnsupportedOperationException("这是常量类，不能被实例化");
    }

    /**
     * 系统管理员角色
     * 拥有系统最高权限，可以管理所有功能
     * 对应数据库 users 表中的 role 字段值
     */
    public static final String ADMIN = "admin";

    /**
     * 宿舍管理员角色
     * 只能管理自己管辖范围内的宿舍
     * 对应数据库 users 表中的 role 字段值
     */
    public static final String MANAGER = "manager";

    /**
     * 学生角色
     * 可以查看宿舍、选宿舍、申请换宿舍
     * 对应数据库 users 表中的 role 字段值
     */
    public static final String STUDENT = "student";

    // ==================== 角色名称（用于前端展示） ====================

    /**
     * 角色名称映射
     * 用于前端显示友好的角色名称
     */
    public static final String ADMIN_NAME = "系统管理员";
    public static final String MANAGER_NAME = "宿舍管理员";
    public static final String STUDENT_NAME = "学生";

    /**
     * 根据角色代码获取角色名称
     * 
     * @param role 角色代码（admin/manager/student）
     * @return 角色中文名称
     */
    public static String getRoleName(String role) {
        if (ADMIN.equals(role)) {
            return ADMIN_NAME;
        } else if (MANAGER.equals(role)) {
            return MANAGER_NAME;
        } else if (STUDENT.equals(role)) {
            return STUDENT_NAME;
        }
        return "未知角色";
    }

    /**
     * 判断是否为管理员角色（包括系统管理员和宿舍管理员）
     * 
     * @param role 角色代码
     * @return true-是管理员，false-不是管理员
     */
    public static boolean isAdminRole(String role) {
        return ADMIN.equals(role) || MANAGER.equals(role);
    }

    /**
     * 判断角色是否有效
     * 
     * @param role 角色代码
     * @return true-有效，false-无效
     */
    public static boolean isValidRole(String role) {
        return ADMIN.equals(role) || MANAGER.equals(role) || STUDENT.equals(role);
    }
}