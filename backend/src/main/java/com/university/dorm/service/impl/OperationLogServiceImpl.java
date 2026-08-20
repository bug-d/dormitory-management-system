package com.university.dorm.service.impl;

import com.university.dorm.entity.OperationLog;
import com.university.dorm.mapper.OperationLogMapper;
import com.university.dorm.service.OperationLogService;
import com.university.dorm.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public void saveLog(Long userId, String username, String operationType, String targetType, Long targetId, String operationDetail) {
        try {
            OperationLog logEntity = new OperationLog();
            logEntity.setUserId(userId != null ? userId.intValue() : null);
            logEntity.setUsername(username);
            logEntity.setOperationType(operationType);
            logEntity.setTargetType(targetType);
            logEntity.setTargetId(targetId != null ? targetId.intValue() : null);
            logEntity.setOperationDetail(operationDetail);

            // 获取IP地址
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String ip = getClientIp(request);
                    logEntity.setIpAddress(ip);
                    logEntity.setUserAgent(request.getHeader("User-Agent"));
                }
            } catch (Exception e) {
                log.warn("获取请求信息失败: {}", e.getMessage());
            }

            operationLogMapper.insert(logEntity);
            log.info("操作日志保存成功: {} - {}", username, operationDetail);

        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public void saveLog(String operationType, String targetType, Long targetId, String operationDetail) {
        try {
            Long userId = null;
            String username = "系统";

            try {
                userId = securityUtil.getCurrentUserId();
                // 根据 userId 获取用户名
                // 这里可以通过 UserMapper 查询，或者直接使用 securityUtil 获取
                username = "用户" + userId;
            } catch (Exception e) {
                // 未登录用户操作
                username = "系统";
            }

            saveLog(userId, username, operationType, targetType, targetId, operationDetail);

        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}