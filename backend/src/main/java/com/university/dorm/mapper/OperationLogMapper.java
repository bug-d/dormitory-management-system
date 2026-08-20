package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 操作日志 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/OperationLogMapper.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 获取最近动态（用于首页）
     */
    @Select("SELECT " +
            "l.id, " +
            "l.username, " +
            "l.operation_type, " +
            "l.operation_detail, " +
            "l.created_at as time, " +
            "CASE " +
            "  WHEN l.operation_type = 'LOGIN' THEN '已完成' " +
            "  WHEN l.operation_type = 'APPLY' THEN '待处理' " +
            "  WHEN l.operation_type = 'AUDIT' THEN '已完成' " +
            "  WHEN l.operation_type = 'CHECKIN' THEN '已完成' " +
            "  WHEN l.operation_type = 'TRANSFER' THEN '待处理' " +
            "  WHEN l.operation_type = 'CHECKOUT' THEN '已完成' " +
            "  ELSE '已完成' " +
            "END as status, " +
            "CASE " +
            "  WHEN l.operation_type = 'LOGIN' THEN 'login' " +
            "  WHEN l.operation_type = 'APPLY' THEN 'apply' " +
            "  WHEN l.operation_type = 'AUDIT' THEN 'audit' " +
            "  WHEN l.operation_type = 'CHECKIN' THEN 'checkin' " +
            "  WHEN l.operation_type = 'TRANSFER' THEN 'transfer' " +
            "  WHEN l.operation_type = 'CHECKOUT' THEN 'checkout' " +
            "  ELSE 'login' " +
            "END as type " +
            "FROM operation_logs l " +
            "ORDER BY l.created_at DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectRecentActivities(@Param("limit") int limit);

    /**
     * 根据用户ID查询操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据操作类型查询日志
     */
    @Select("SELECT * FROM operation_logs WHERE operation_type = #{operationType} ORDER BY created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectByOperationType(@Param("operationType") String operationType, @Param("limit") int limit);
}