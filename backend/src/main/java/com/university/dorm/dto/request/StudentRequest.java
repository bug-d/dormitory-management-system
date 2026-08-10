package com.university.dorm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 学生请求 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/StudentRequest.java
 * 作用：接收前端添加/更新学生时的请求参数
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class StudentRequest {

    /**
     * 学生ID（更新时必传）
     */
    private Long id;

    /**
     * 学号（不能为空）
     */
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    /**
     * 姓名（不能为空）
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 性别：M-男，F-女（不能为空）
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^[MF]$", message = "性别只能为 M 或 F")
    private String gender;

    /**
     * 年级（不能为空）
     */
    @NotBlank(message = "年级不能为空")
    private String grade;

    /**
     * 专业（不能为空）
     */
    @NotBlank(message = "专业不能为空")
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 身份证号
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$", 
             message = "身份证号格式不正确")
    private String idCard;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系电话格式不正确")
    private String emergencyPhone;

    /**
     * 是否新生：Y-是，N-否
     */
    @Pattern(regexp = "^[YN]$", message = "是否新生只能为 Y 或 N")
    private String isNew;

    /**
     * 入学日期
     */
    private String enrollmentDate;
}