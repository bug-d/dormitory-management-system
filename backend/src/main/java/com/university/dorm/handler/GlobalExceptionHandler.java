package com.university.dorm.handler;

import com.university.dorm.dto.response.Result;
import com.university.dorm.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/handler/GlobalExceptionHandler.java
 * 作用：统一捕获和处理系统中的所有异常，返回统一格式的响应
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    /**
     * 处理业务异常（自定义异常）
     *
     * @param e BusinessException
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // ==================== 参数校验异常 ====================

    /**
     * 处理参数校验异常（@Valid 校验失败）
     *
     * @param e MethodArgumentNotValidException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.paramError(message);
    }

    /**
     * 处理绑定异常（表单绑定失败）
     *
     * @param e BindException
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return Result.paramError(message);
    }

    /**
     * 处理参数缺失异常
     *
     * @param e MissingServletRequestParameterException
     * @return Result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getMessage());
        return Result.paramError("缺少请求参数: " + e.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常
     *
     * @param e MethodArgumentTypeMismatchException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return Result.paramError("参数类型不匹配: " + e.getName());
    }

    /**
     * 处理请求体不可读异常（JSON 格式错误）
     *
     * @param e HttpMessageNotReadableException
     * @return Result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return Result.paramError("请求体格式错误，请检查 JSON 格式");
    }

    // ==================== 认证授权异常 ====================

    /**
     * 处理认证异常（未登录或 Token 无效）
     *
     * @param e BadCredentialsException
     * @return Result
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证失败: {}", e.getMessage());
        return Result.unauthorized("用户名或密码错误");
    }

    /**
     * 处理访问拒绝异常（无权限）
     *
     * @param e AccessDeniedException
     * @return Result
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝: {}", e.getMessage());
        return Result.forbidden("没有权限执行此操作");
    }

    // ==================== 请求异常 ====================

    /**
     * 处理请求方法不支持异常
     *
     * @param e HttpRequestMethodNotSupportedException
     * @return Result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return Result.error(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理 404 异常
     *
     * @param e NoHandlerFoundException
     * @return Result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getRequestURL());
        return Result.notFound("接口不存在: " + e.getRequestURL());
    }

    // ==================== 系统异常 ====================

    /**
     * 处理空指针异常
     *
     * @param e NullPointerException
     * @return Result
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: ", e);
        return Result.internalError("系统内部错误，请稍后重试");
    }

    /**
     * 处理其他所有未捕获的异常
     *
     * @param e Exception
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.internalError("系统内部错误，请稍后重试");
    }
}