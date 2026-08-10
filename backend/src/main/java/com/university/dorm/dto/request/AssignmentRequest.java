package com.university.dorm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 入住申请请求 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/AssignmentRequest.java
 * 作用：接收学生选宿舍/换宿舍时的请求参数
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class AssignmentRequest {

    /**
     * 宿舍ID（不能为空）
     */
    @NotNull(message = "宿舍ID不能为空")
    private Long dormId;

    /**
     * 床号（A/B/C/D）
     */
    @NotBlank(message = "床号不能为空")
    @Pattern(regexp = "^[A-D]$", message = "床号只能为 A、B、C、D")
    private String bedNo;

    /**
     * 申请类型：new_checkin-新生入住，transfer-调宿
     */
    private String type;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 学期
     */
    private String semester;
}