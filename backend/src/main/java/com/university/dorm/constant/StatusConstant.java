package com.university.dorm.constant;

/**
 * 状态常量类
 * 
 * 路径：backend/src/main/java/com/university/dorm/constant/StatusConstant.java
 * 作用：定义系统中所有状态，统一管理，避免硬编码
 * 
 * @author University Dorm Team
 * @version 1.0.0
 */
public class StatusConstant {

    /**
     * 私有构造函数，防止实例化
     */
    private StatusConstant() {
        throw new UnsupportedOperationException("这是常量类，不能被实例化");
    }

    // ==================== 用户状态 ====================

    /** 用户状态：启用 */
    public static final Integer USER_ENABLED = 1;

    /** 用户状态：禁用 */
    public static final Integer USER_DISABLED = 0;

    // ==================== 学生状态 ====================

    /** 学生状态：在读 */
    public static final Integer STUDENT_ACTIVE = 1;

    /** 学生状态：已毕业 */
    public static final Integer STUDENT_GRADUATED = 0;

    /** 学生状态：休学 */
    public static final Integer STUDENT_SUSPENDED = 2;

    /** 是否新生：是 */
    public static final String IS_NEW_YES = "Y";

    /** 是否新生：否 */
    public static final String IS_NEW_NO = "N";

    // ==================== 宿舍状态 ====================

    /** 宿舍状态：可用 */
    public static final String DORM_AVAILABLE = "available";

    /** 宿舍状态：已满 */
    public static final String DORM_FULL = "full";

    /** 宿舍状态：维修中 */
    public static final String DORM_MAINTENANCE = "maintenance";

    /** 宿舍状态：已关闭 */
    public static final String DORM_CLOSED = "closed";

    // ==================== 入住申请状态 ====================

    /** 申请状态：待审核 */
    public static final String ASSIGNMENT_PENDING = "pending";

    /** 申请状态：已通过 */
    public static final String ASSIGNMENT_APPROVED = "approved";

    /** 申请状态：已驳回 */
    public static final String ASSIGNMENT_REJECTED = "rejected";

    /** 申请状态：已入住 */
    public static final String ASSIGNMENT_ACTIVE = "active";

    /** 申请状态：已退宿 */
    public static final String ASSIGNMENT_LEFT = "left";

    /** 申请状态：已取消 */
    public static final String ASSIGNMENT_CANCELED = "canceled";

    // ==================== 申请类型 ====================

    /** 申请类型：新生入住 */
    public static final String TYPE_NEW_CHECKIN = "new_checkin";

    /** 申请类型：调宿 */
    public static final String TYPE_TRANSFER = "transfer";

    /** 申请类型：毕业离校 */
    public static final String TYPE_GRADUATE_LEAVE = "graduate_leave";

    /** 申请类型：其他 */
    public static final String TYPE_OTHER = "other";

    // ==================== 权限类型 ====================

    /** 权限类型：完全控制 */
    public static final String PERMISSION_FULL = "full";

    /** 权限类型：只读 */
    public static final String PERMISSION_READONLY = "readonly";

    // ==================== 性别 ====================

    /** 性别：男 */
    public static final String GENDER_MALE = "M";

    /** 性别：女 */
    public static final String GENDER_FEMALE = "F";

    /** 性别名称：男 */
    public static final String GENDER_MALE_NAME = "男";

    /** 性别名称：女 */
    public static final String GENDER_FEMALE_NAME = "女";

    // ==================== 系统配置键 ====================

    /** 系统配置：当前学期 */
    public static final String CONFIG_SEMESTER = "semester";

    /** 系统配置：选宿舍开始时间 */
    public static final String CONFIG_SELECT_START = "select_start_time";

    /** 系统配置：选宿舍结束时间 */
    public static final String CONFIG_SELECT_END = "select_end_time";

    /** 系统配置：最大床位 */
    public static final String CONFIG_MAX_CAPACITY = "max_dorm_capacity";

    /** 系统配置：初始密码 */
    public static final String CONFIG_INIT_PASSWORD = "init_password";

    // ==================== 工具方法 ====================

    /**
     * 获取申请状态的中文名称
     * 
     * @param status 状态代码
     * @return 中文名称
     */
    public static String getAssignmentStatusName(String status) {
        switch (status) {
            case ASSIGNMENT_PENDING:
                return "待审核";
            case ASSIGNMENT_APPROVED:
                return "已通过";
            case ASSIGNMENT_REJECTED:
                return "已驳回";
            case ASSIGNMENT_ACTIVE:
                return "已入住";
            case ASSIGNMENT_LEFT:
                return "已退宿";
            case ASSIGNMENT_CANCELED:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取宿舍状态的中文名称
     */
    public static String getDormStatusName(String status) {
        switch (status) {
            case DORM_AVAILABLE:
                return "可用";
            case DORM_FULL:
                return "已满";
            case DORM_MAINTENANCE:
                return "维修中";
            case DORM_CLOSED:
                return "已关闭";
            default:
                return "未知状态";
        }
    }

    /**
     * 判断申请状态是否为终态（不可再变更）
     */
    public static boolean isFinalStatus(String status) {
        return ASSIGNMENT_APPROVED.equals(status)
                || ASSIGNMENT_REJECTED.equals(status)
                || ASSIGNMENT_LEFT.equals(status)
                || ASSIGNMENT_CANCELED.equals(status);
    }

    /**
     * 判断宿舍是否可入住
     */
    public static boolean isDormAvailable(String status) {
        return DORM_AVAILABLE.equals(status);
    }

    /**
     * 判断性别代码是否有效
     */
    public static boolean isValidGender(String gender) {
        return GENDER_MALE.equals(gender) || GENDER_FEMALE.equals(gender);
    }
}