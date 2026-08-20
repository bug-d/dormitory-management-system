package com.university.dorm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordUtil {

    /**
     * BCrypt 密码编码器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 初始密码（新增用户时使用）
     */
    @Value("${system.init-password:123456}")
    private String initPassword;

    /**
     * 重置密码（重置用户时使用）
     */
    @Value("${system.reset-password:123456}")
    private String resetPassword;

    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断密码是否需要重新加密
     */
    public boolean upgradeEncoding(String encodedPassword) {
        return passwordEncoder.upgradeEncoding(encodedPassword);
    }

    /**
     * 生成随机密码（6位数字）
     */
    public String generateRandomPassword() {
        int length = 6;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * 生成随机密码（指定长度）
     */
    public String generateRandomPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("密码长度必须大于0");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * 生成包含字母和数字的随机密码
     */
    public String generateStrongPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("密码长度必须大于0");
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 获取初始密码
     */
    public String getInitPassword() {
        return initPassword;
    }

    /**
     * 获取重置密码
     */
    public String getResetPassword() {
        return resetPassword;
    }
}