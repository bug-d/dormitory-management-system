package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.ManagerPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 宿舍管理员权限 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/ManagerPermissionMapper.java
 * 作用：管理员权限数据访问层，继承 BaseMapper 自动获得 CRUD 方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface ManagerPermissionMapper extends BaseMapper<ManagerPermission> {

    /**
     * 根据管理员ID查询其管辖的所有宿舍ID
     *
     * @param managerId 管理员用户ID
     * @return 宿舍ID列表
     */
    @Select("SELECT dorm_id FROM manager_permissions WHERE manager_id = #{managerId}")
    List<Long> selectDormIdsByManagerId(@Param("managerId") Long managerId);

    /**
     * 根据管理员ID查询其管辖的所有权限记录
     *
     * @param managerId 管理员用户ID
     * @return 权限记录列表
     */
    @Select("SELECT * FROM manager_permissions WHERE manager_id = #{managerId}")
    List<ManagerPermission> selectByManagerId(@Param("managerId") Long managerId);

    /**
     * 根据宿舍ID查询所有管理员
     *
     * @param dormId 宿舍ID
     * @return 权限记录列表
     */
    @Select("SELECT * FROM manager_permissions WHERE dorm_id = #{dormId}")
    List<ManagerPermission> selectByDormId(@Param("dormId") Long dormId);

    /**
     * 检查管理员是否有某个宿舍的权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return 存在返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM manager_permissions WHERE manager_id = #{managerId} AND dorm_id = #{dormId}")
    boolean hasPermission(@Param("managerId") Long managerId, @Param("dormId") Long dormId);

    /**
     * 检查管理员是否有某个宿舍的完全控制权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return 存在返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM manager_permissions WHERE manager_id = #{managerId} AND dorm_id = #{dormId} AND permission_type = 'full'")
    boolean hasFullPermission(@Param("managerId") Long managerId, @Param("dormId") Long dormId);

    /**
     * 删除管理员的所有权限
     *
     * @param managerId 管理员用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM manager_permissions WHERE manager_id = #{managerId}")
    int deleteByManagerId(@Param("managerId") Long managerId);

    /**
     * 删除某个宿舍的所有管理员权限
     *
     * @param dormId 宿舍ID
     * @return 影响行数
     */
    @Delete("DELETE FROM manager_permissions WHERE dorm_id = #{dormId}")
    int deleteByDormId(@Param("dormId") Long dormId);

    /**
     * 删除管理员的某个宿舍权限
     *
     * @param managerId 管理员用户ID
     * @param dormId    宿舍ID
     * @return 影响行数
     */
    @Delete("DELETE FROM manager_permissions WHERE manager_id = #{managerId} AND dorm_id = #{dormId}")
    int deleteByManagerIdAndDormId(@Param("managerId") Long managerId, @Param("dormId") Long dormId);

    /**
     * 统计某个管理员管辖的宿舍数量
     *
     * @param managerId 管理员用户ID
     * @return 宿舍数量
     */
    @Select("SELECT COUNT(*) FROM manager_permissions WHERE manager_id = #{managerId}")
    Long countByManagerId(@Param("managerId") Long managerId);

    /**
     * 统计某个宿舍被多少个管理员管辖
     *
     * @param dormId 宿舍ID
     * @return 管理员数量
     */
    @Select("SELECT COUNT(*) FROM manager_permissions WHERE dorm_id = #{dormId}")
    Long countByDormId(@Param("dormId") Long dormId);

    /**
     * 批量插入权限记录
     *
     * @param list 权限记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ManagerPermission> list);
}