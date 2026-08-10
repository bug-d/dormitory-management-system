package com.university.dorm.dto.response;

import lombok.Data;

/**
 * 统一响应结果 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/response/Result.java
 * 作用：封装所有 API 返回数据，统一响应格式
 * <p>
 * 响应格式：
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": { ... }
 * }
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class Result<T> {

    /**
     * 状态码：200-成功，400-参数错误，401-未登录，403-无权限，404-资源不存在，500-服务器错误
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    // ==================== 成功响应 ====================

    /**
     * 成功响应（无数据）
     *
     * @return Result 对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 返回数据
     * @return Result 对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带数据和自定义消息）
     *
     * @param message 自定义消息
     * @param data    返回数据
     * @return Result 对象
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // ==================== 错误响应 ====================

    /**
     * 错误响应（默认错误码400）
     *
     * @param message 错误信息
     * @return Result 对象
     */
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }

    /**
     * 错误响应（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     * @return Result 对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // ==================== 常用错误快捷方法 ====================

    /**
     * 参数错误（400）
     */
    public static <T> Result<T> paramError(String message) {
        return error(400, message);
    }

    /**
     * 未登录（401）
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 无权限（403）
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 业务冲突（409）
     */
    public static <T> Result<T> conflict(String message) {
        return error(409, message);
    }

    /**
     * 服务器错误（500）
     */
    public static <T> Result<T> internalError(String message) {
        return error(500, message);
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否成功
     *
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
