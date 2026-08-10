package com.university.dorm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 密码工具类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/util/PasswordUtil.java
 * 作用：密码加密和验证（使用 BCrypt 加密算法）
 * <p>
 * BCrypt 特点：
 * - 每次加密结果不同（自动加盐）
 * - 无法反向解密（单向哈希）
 * - 安全性高，广泛用于密码存储
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Component
public class PasswordUtil {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * BCrypt 密码编码器
     * 加密强度：10（默认，范围 4-31，越大越安全但越慢）
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword      明文密码
     * @param encodedPassword  加密后的密码
     * @return true-匹配，false-不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断密码是否需要重新加密
     * 如果加密强度已升级，可以重新加密
     *
     * @param encodedPassword 加密后的密码
     * @return true-需要重新加密，false-不需要
     */
    public boolean upgradeEncoding(String encodedPassword) {
        return passwordEncoder.upgradeEncoding(encodedPassword);
    }

    /**
     * 生成随机密码（用于重置密码）
     *
     * @return 6位随机数字密码
     */
    public String generateRandomPassword() {
        int length = 6;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成随机密码（指定长度）
     *
     * @param length 密码长度
     * @return 随机数字密码
     */
    public String generateRandomPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("密码长度必须大于0");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成包含字母和数字的随机密码
     *
     * @param length 密码长度
     * @return 随机密码（字母+数字）
     */
    public String generateStrongPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("密码长度必须大于0");
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
