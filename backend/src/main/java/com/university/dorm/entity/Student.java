package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/Student.java
 * 作用：对应数据库 students 表，存储学生详细信息
 * <p>
 * 表结构：
 * - id: 学生ID（主键，自增）
 * - user_id: 关联用户ID（外键 → users.id）
 * - student_no: 学号（唯一）
 * - name: 姓名
 * - gender: 性别（M-男，F-女）
 * - grade: 年级
 * - major: 专业
 * - class_name: 班级
 * - id_card: 身份证号
 * - phone: 联系电话
 * - emergency_contact: 紧急联系人
 * - emergency_phone: 紧急联系电话
 * - is_new: 是否新生（Y-是，N-否）
 * - enrollment_date: 入学日期
 * - status: 状态（1-在读，0-已毕业，2-休学）
 * - created_at: 创建时间
 * - updated_at: 更新时间
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("students")
public class Student {

    /**
     * 学生ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID（外键 → users.id）
     * 一个学生对应一个登录账号
     */
    private Long userId;

    /**
     * 学号（唯一）
     */
    private String studentNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：M-男，F-女
     *
     * @see com.university.dorm.constant.StatusConstant#GENDER_MALE
     * @see com.university.dorm.constant.StatusConstant#GENDER_FEMALE
     */
    private String gender;

    /**
     * 年级（如：2024）
     */
    private String grade;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 是否新生：Y-是，N-否
     *
     * @see com.university.dorm.constant.StatusConstant#IS_NEW_YES
     * @see com.university.dorm.constant.StatusConstant#IS_NEW_NO
     */
    private String isNew;

    /**
     * 入学日期
     */
    private LocalDate enrollmentDate;

    /**
     * 状态：1-在读，0-已毕业，2-休学
     *
     * @see com.university.dorm.constant.StatusConstant#STUDENT_ACTIVE
     * @see com.university.dorm.constant.StatusConstant#STUDENT_GRADUATED
     * @see com.university.dorm.constant.StatusConstant#STUDENT_SUSPENDED
     */
    private Integer status;

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
}