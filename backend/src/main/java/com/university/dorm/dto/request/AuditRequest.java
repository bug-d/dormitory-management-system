package com.university.dorm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 审核请求 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/AuditRequest.java
 * 作用：接收管理员审核时的请求参数（通过/驳回）
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class AuditRequest {

    /**
     * 申请记录ID（不能为空）
     */
    @NotNull(message = "申请ID不能为空")
    private Long assignmentId;

    /**
     * 审核动作：approve-通过，reject-驳回（不能为空）
     */
    @NotBlank(message = "审核动作不能为空")
    @Pattern(regexp = "^(approve|reject)$", message = "审核动作只能为 approve 或 reject")
    private String action;

    /**
     * 审核备注（驳回时建议填写原因）
     */
    private String remark;
}