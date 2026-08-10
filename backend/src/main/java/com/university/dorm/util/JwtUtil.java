package com.university.dorm.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/util/JwtUtil.java
 * 作用：生成 Token、解析 Token、验证 Token
 * <p>
 * 使用 jjwt 0.12.x 版本 API
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT 密钥（从配置文件读取）
     * 注意：生产环境必须使用强密钥！
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token 过期时间（从配置文件读取）
     * 默认：24小时（单位：毫秒）
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * Token 头部名称（从配置文件读取）
     */
    @Value("${jwt.header:Authorization}")
    private String header;

    /**
     * Token 前缀（从配置文件读取）
     */
    @Value("${jwt.prefix:Bearer}")
    private String prefix;

    /**
     * 签名密钥对象
     */
    private SecretKey secretKey;

    /**
     * 初始化：根据密钥字符串生成 SecretKey 对象
     * 在 Spring 容器加载完成后自动执行
     */
    @PostConstruct
    public void init() {
        // 使用 HMAC-SHA256 算法生成密钥
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT 工具类初始化成功");
    }

    // ==================== 生成 Token ====================

    /**
     * 生成 JWT Token
     *
     * @param userId 用户ID
     * @param role   用户角色（admin/manager/student）
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String role) {
        // 当前时间
        Date now = new Date();
        // 过期时间 = 当前时间 + 配置的过期时长
        Date expiryDate = new Date(now.getTime() + expiration);

        // 构建 JWT
        return Jwts.builder()
                // 设置用户ID（主题）
                .subject(String.valueOf(userId))
                // 设置角色（自定义字段）
                .claim("role", role)
                // 设置签发时间
                .issuedAt(now)
                // 设置过期时间
                .expiration(expiryDate)
                // 设置签发者
                .issuer("dormitory-system")
                // 签名：使用密钥和 HS256 算法
                .signWith(secretKey, Jwts.SIG.HS256)
                // 压缩成字符串
                .compact();
    }

    /**
     * 生成 JWT Token（带额外自定义字段）
     *
     * @param userId   用户ID
     * @param role     用户角色
     * @param extraKey 额外的自定义字段键
     * @param extraValue 额外的自定义字段值
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String role, String extraKey, Object extraValue) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim(extraKey, extraValue)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer("dormitory-system")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 生成 JWT Token（带多个自定义字段）
     *
     * @param userId 用户ID
     * @param claims 自定义字段 Map
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer("dormitory-system")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // ==================== 验证和解析 Token ====================

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            // 尝试解析 Token，如果不抛异常说明有效
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析 Token，获取所有声明（Claims）
     *
     * @param token JWT Token
     * @return Claims 对象
     * @throws io.jsonwebtoken.JwtException 如果 Token 无效或过期
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ==================== 获取 Token 中的信息 ====================

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从 Token 中获取用户角色
     *
     * @param token JWT Token
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 从 Token 中获取某个字段的值
     *
     * @param token JWT Token
     * @param key   字段名
     * @param clazz 返回类型
     * @return 字段值
     */
    public <T> T getClaimFromToken(String token, String key, Class<T> clazz) {
        Claims claims = parseToken(token);
        return claims.get(key, clazz);
    }

    /**
     * 从 Token 中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 从 Token 中获取签发时间
     *
     * @param token JWT Token
     * @return 签发时间
     */
    public Date getIssuedAtFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getIssuedAt();
    }

    /**
     * 检查 Token 是否已过期
     *
     * @param token JWT Token
     * @return true-已过期，false-未过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    // ==================== Token 处理 ====================

    /**
     * 去除 Token 前缀（Bearer ）
     * 如果 Token 以 "Bearer " 开头，则去掉前缀
     *
     * @param token 原始 Token（可能带前缀）
     * @return 纯净的 Token
     */
    public String removePrefix(String token) {
        if (token != null && token.startsWith(prefix + " ")) {
            return token.substring(prefix.length() + 1);
        }
        return token;
    }

    /**
     * 获取 Header 名称
     *
     * @return Header 名称
     */
    public String getHeader() {
        return header;
    }

    /**
     * 获取 Token 前缀
     *
     * @return Token 前缀
     */
    public String getPrefix() {
        return prefix;
    }

    // ==================== 刷新 Token ====================

    /**
     * 刷新 Token（延长过期时间）
     *
     * @param oldToken 旧的 Token
     * @return 新的 Token
     */
    public String refreshToken(String oldToken) {
        Claims claims = parseToken(oldToken);
        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);

        // 提取其他自定义字段（排除默认字段）
        Map<String, Object> extraClaims = new HashMap<>();
        claims.forEach((key, value) -> {
            if (!"sub".equals(key) && !"iat".equals(key)
                    && !"exp".equals(key) && !"iss".equals(key)
                    && !"role".equals(key)) {
                extraClaims.put(key, value);
            }
        });
        extraClaims.put("role", role);

        return generateToken(userId, extraClaims);
    }
}
