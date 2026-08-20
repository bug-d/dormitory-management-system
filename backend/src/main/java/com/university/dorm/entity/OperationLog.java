package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/OperationLog.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("operation_logs")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String username;

    private String operationType;

    private String targetType;

    private Integer targetId;

    private String operationDetail;

    private String ipAddress;

    private String userAgent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}