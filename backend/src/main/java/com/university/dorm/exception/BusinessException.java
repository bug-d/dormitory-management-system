package com.university.dorm.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 
 * 路径：backend/src/main/java/com/university/dorm/exception/BusinessException.java
 * 作用：处理业务逻辑异常，如数据校验失败、业务规则冲突等
 * 
 * 使用场景：
 * - 宿舍已满，无法入住
 * - 学号已存在，无法重复添加
 * - 学生已有宿舍，不能重复选择
 * - 选宿舍时间已过，无法选择
 * 
 * @author University Dorm Team
 * @version 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     * 默认：400（业务错误）
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 构造函数：只传错误信息（默认错误码400）
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    /**
     * 构造函数：传错误码和错误信息
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数：传错误信息和原因
     * 
     * @param message 错误信息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
        this.message = message;
    }

    /**
     * 构造函数：传错误码、错误信息和原因
     * 
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    // ==================== 常用业务错误码 ====================

    /** 参数错误 */
    public static final Integer CODE_PARAM_ERROR = 400;

    /** 资源不存在 */
    public static final Integer CODE_NOT_FOUND = 404;

    /** 业务冲突（如宿舍已满） */
    public static final Integer CODE_CONFLICT = 409;

    /** 数据重复（如学号已存在） */
    public static final Integer CODE_DUPLICATE = 409;

    /** 操作不允许（如时间已过） */
    public static final Integer CODE_FORBIDDEN = 403;

    /** 系统内部错误 */
    public static final Integer CODE_INTERNAL_ERROR = 500;

    // ==================== 快捷工厂方法 ====================

    /**
     * 参数错误异常
     * 
     * @param message 错误信息
     * @return BusinessException
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(CODE_PARAM_ERROR, message);
    }

    /**
     * 资源不存在异常
     * 
     * @param message 错误信息
     * @return BusinessException
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(CODE_NOT_FOUND, message);
    }

    /**
     * 业务冲突异常
     * 
     * @param message 错误信息
     * @return BusinessException
     */
    public static BusinessException conflict(String message) {
        return new BusinessException(CODE_CONFLICT, message);
    }

    /**
     * 数据重复异常
     * 
     * @param message 错误信息
     * @return BusinessException
     */
    public static BusinessException duplicate(String message) {
        return new BusinessException(CODE_DUPLICATE, message);
    }

    /**
     * 操作禁止异常
     * 
     * @param message 错误信息
     * @return BusinessException
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(CODE_FORBIDDEN, message);
    }
}