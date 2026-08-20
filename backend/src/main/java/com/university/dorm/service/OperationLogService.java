package com.university.dorm.service;

import com.university.dorm.entity.OperationLog;

/**
 * 操作日志服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/OperationLogService.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface OperationLogService {

    /**
     * 保存操作日志
     *
     * @param userId         用户ID
     * @param username       用户名
     * @param operationType  操作类型（LOGIN/APPLY/AUDIT/CHECKIN/CHECKOUT/TRANSFER）
     * @param targetType     目标类型（USER/STUDENT/DORM/ASSIGNMENT）
     * @param targetId       目标ID
     * @param operationDetail 操作详情
     */
    void saveLog(Long userId, String username, String operationType, String targetType, Long targetId, String operationDetail);

    /**
     * 保存操作日志（简化版，自动获取当前用户）
     */
    void saveLog(String operationType, String targetType, Long targetId, String operationDetail);
}