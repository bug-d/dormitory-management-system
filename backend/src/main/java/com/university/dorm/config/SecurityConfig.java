package com.university.dorm.config;

import com.university.dorm.constant.RoleConstant;
import com.university.dorm.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/config/SecurityConfig.java
 * 作用：配置认证、授权、密码加密、JWT 过滤器
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器（BCrypt 加密）
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);
    }

    /**
     * 认证管理器
     *
     * @param configuration AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 安全过滤器链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（使用 JWT 不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 禁用 Session（使用 JWT 无状态）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 请求授权配置
                .authorizeHttpRequests(auth -> auth
                        // 公共接口：无需认证
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/doc.html",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()
                        // 管理员接口：需要 ADMIN 角色
                        .requestMatchers("/admin/**").hasRole(RoleConstant.ADMIN)
                        // 宿舍管理员接口：需要 MANAGER 或 ADMIN 角色
                        .requestMatchers("/manager/**").hasAnyRole(RoleConstant.MANAGER, RoleConstant.ADMIN)
                        // 学生接口：需要 STUDENT 或 ADMIN 角色
                        .requestMatchers("/student/**").hasAnyRole(RoleConstant.STUDENT, RoleConstant.ADMIN)
                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )

                // 4. 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}