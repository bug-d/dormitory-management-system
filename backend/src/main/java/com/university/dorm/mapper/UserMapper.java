package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/UserMapper.java
 * 作用：用户数据访问层，继承 BaseMapper 自动获得 CRUD 方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包含逻辑删除过滤）
     *
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND status != 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户名和密码查询用户（用于登录验证）
     *
     * @param username 用户名
     * @param password 密码（加密后）
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password} AND status = 1")
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    void updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 新密码（加密后）
     * @return 影响行数
     */
    @Update("UPDATE users SET password = #{password} WHERE id = #{userId}")
    int resetPassword(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE users SET status = 0 WHERE id = #{userId}")
    int disableUser(@Param("userId") Long userId);

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE users SET status = 1 WHERE id = #{userId}")
    int enableUser(@Param("userId") Long userId);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
}