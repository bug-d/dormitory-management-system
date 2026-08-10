package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.university.dorm.constant.StatusConstant;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住记录实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/DormAssignment.java
 * 作用：对应数据库 dorm_assignments 表，记录所有入住和申请记录
 * <p>
 * 表结构：
 * - id: 记录ID（主键，自增）
 * - student_id: 学生ID（外键 → students.id）
 * - dorm_id: 宿舍ID（外键 → dormitories.id）
 * - bed_no: 床号（A/B/C/D）
 * - start_date: 入住开始日期
 * - end_date: 入住结束日期（NULL表示当前入住）
 * - status: 状态（pending-待审核，approved-已通过，rejected-已驳回，active-已入住，left-已退宿，canceled-已取消）
 * - type: 类型（new_checkin-新生入住，transfer-调宿，graduate_leave-毕业离校，other-其他）
 * - semester: 学期
 * - apply_reason: 申请理由
 * - audit_time: 审核时间
 * - auditor_id: 审核人ID（外键 → users.id）
 * - audit_remark: 审核备注
 * - created_at: 创建时间
 * - updated_at: 更新时间
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("dorm_assignments")
public class DormAssignment {

    /**
     * 记录ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID（外键 → students.id）
     */
    private Long studentId;

    /**
     * 宿舍ID（外键 → dormitories.id）
     */
    private Long dormId;

    /**
     * 床号（A/B/C/D）
     */
    private String bedNo;

    /**
     * 入住开始日期
     */
    private LocalDate startDate;

    /**
     * 入住结束日期（NULL表示当前入住）
     */
    private LocalDate endDate;

    /**
     * 状态：
     * pending-待审核，approved-已通过，rejected-已驳回，
     * active-已入住，left-已退宿，canceled-已取消
     *
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_PENDING
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_APPROVED
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_REJECTED
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_ACTIVE
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_LEFT
     * @see com.university.dorm.constant.StatusConstant#ASSIGNMENT_CANCELED
     */
    private String status;

    /**
     * 类型：
     * new_checkin-新生入住，transfer-调宿，
     * graduate_leave-毕业离校，other-其他
     *
     * @see com.university.dorm.constant.StatusConstant#TYPE_NEW_CHECKIN
     * @see com.university.dorm.constant.StatusConstant#TYPE_TRANSFER
     * @see com.university.dorm.constant.StatusConstant#TYPE_GRADUATE_LEAVE
     * @see com.university.dorm.constant.StatusConstant#TYPE_OTHER
     */
    private String type;

    /**
     * 学期（如：2026-2027-1）
     */
    private String semester;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人ID（外键 → users.id）
     */
    private Long auditorId;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== 辅助方法 ====================

    /**
     * 判断是否为待审核状态
     *
     * @return true-待审核，false-不是
     */
    public boolean isPending() {
        return StatusConstant.ASSIGNMENT_PENDING.equals(this.status);
    }

    /**
     * 判断是否为已入住状态
     *
     * @return true-已入住，false-不是
     */
    public boolean isActive() {
        return StatusConstant.ASSIGNMENT_ACTIVE.equals(this.status);
    }

    /**
     * 判断是否为终态（不可再变更）
     *
     * @return true-终态，false-非终态
     */
    public boolean isFinalStatus() {
        return StatusConstant.ASSIGNMENT_APPROVED.equals(this.status)
                || StatusConstant.ASSIGNMENT_REJECTED.equals(this.status)
                || StatusConstant.ASSIGNMENT_LEFT.equals(this.status)
                || StatusConstant.ASSIGNMENT_CANCELED.equals(this.status);
    }

    /**
     * 判断是否为新生入住类型
     *
     * @return true-新生入住，false-不是
     */
    public boolean isNewCheckin() {
        return StatusConstant.TYPE_NEW_CHECKIN.equals(this.type);
    }

    /**
     * 判断是否为调宿类型
     *
     * @return true-调宿，false-不是
     */
    public boolean isTransfer() {
        return StatusConstant.TYPE_TRANSFER.equals(this.type);
    }

    /**
     * 判断当前是否正在入住（未退宿）
     *
     * @return true-正在入住，false-已退宿
     */
    public boolean isCurrentlyLiving() {
        return isActive() && endDate == null;
    }

    /**
     * 获取状态中文名称
     *
     * @return 状态中文名称
     */
    public String getStatusName() {
        return StatusConstant.getAssignmentStatusName(this.status);
    }
}