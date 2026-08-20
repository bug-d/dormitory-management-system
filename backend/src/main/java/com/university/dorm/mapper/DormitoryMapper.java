package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.Dormitory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 宿舍 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/DormitoryMapper.java
 * 作用：宿舍数据访问层，继承 BaseMapper 自动获得 CRUD 方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface DormitoryMapper extends BaseMapper<Dormitory> {

    // ==================== 查询 ====================

    /**
     * 查询所有可用宿舍（状态为 available 且未满）
     */
    @Select("SELECT * FROM dormitories WHERE status = 'available' AND occupied < capacity ORDER BY building_no, room_no")
    List<Dormitory> selectAvailableDorms();

    /**
     * 根据性别查询可用宿舍
     */
    @Select("SELECT * FROM dormitories WHERE gender = #{gender} AND status = 'available' AND occupied < capacity ORDER BY building_no, room_no")
    List<Dormitory> selectAvailableDormsByGender(@Param("gender") String gender);

    /**
     * 根据楼栋查询宿舍
     */
    @Select("SELECT * FROM dormitories WHERE building_no = #{buildingNo} ORDER BY room_no")
    List<Dormitory> selectByBuilding(@Param("buildingNo") String buildingNo);

    /**
     * 根据状态查询宿舍
     */
    @Select("SELECT * FROM dormitories WHERE status = #{status} ORDER BY building_no, room_no")
    List<Dormitory> selectByStatus(@Param("status") String status);

    /**
     * 根据性别查询宿舍
     */
    @Select("SELECT * FROM dormitories WHERE gender = #{gender} ORDER BY building_no, room_no")
    List<Dormitory> selectByGender(@Param("gender") String gender);

    /**
     * 查询所有宿舍楼栋列表
     */
    @Select("SELECT DISTINCT building_no FROM dormitories ORDER BY building_no")
    List<String> selectAllBuildings();

    /**
     * 查询已满的宿舍
     */
    @Select("SELECT * FROM dormitories WHERE occupied >= capacity AND status != 'closed' ORDER BY building_no, room_no")
    List<Dormitory> selectFullDorms();

    /**
     * 查询空宿舍（未入住）
     */
    @Select("SELECT * FROM dormitories WHERE occupied = 0 AND status = 'available' ORDER BY building_no, room_no")
    List<Dormitory> selectEmptyDorms();

    // ==================== 统计（用于首页图表） ====================

    /**
     * 按楼栋统计入住率（用于首页柱状图）
     */
    @Select("SELECT " +
            "building_no as name, " +
            "ROUND(SUM(occupied) * 100.0 / SUM(capacity), 1) as value " +
            "FROM dormitories " +
            "WHERE status != 'closed' " +
            "GROUP BY building_no " +
            "ORDER BY building_no")
    List<Map<String, Object>> selectBuildingStatistics();

    /**
     * 统计总床位、已入住、空床位
     */
    @Select("SELECT SUM(capacity) as total_beds, SUM(occupied) as occupied_beds, SUM(capacity - occupied) as empty_beds FROM dormitories WHERE status != 'closed'")
    Map<String, Object> selectOverallStatistics();

    /**
     * 统计某性别的总床位数和已入住数
     */
    @Select("SELECT SUM(capacity) as total_beds, SUM(occupied) as occupied_beds FROM dormitories WHERE gender = #{gender} AND status != 'closed'")
    Map<String, Object> selectStatisticsByGender(@Param("gender") String gender);

    /**
     * 统计各楼栋宿舍数量
     */
    @Select("SELECT building_no, COUNT(*) as dorm_count, SUM(capacity) as total_beds, SUM(occupied) as occupied_beds " +
            "FROM dormitories WHERE status != 'closed' GROUP BY building_no")
    List<Map<String, Object>> selectBuildingDormCount();

    /**
     * 查询某楼栋的入住率
     */
    @Select("SELECT building_no, COUNT(*) as dorm_count, SUM(capacity) as total_beds, SUM(occupied) as occupied_beds, " +
            "ROUND(SUM(occupied) * 100.0 / SUM(capacity), 2) as occupancy_rate " +
            "FROM dormitories WHERE building_no = #{buildingNo} GROUP BY building_no")
    Map<String, Object> selectBuildingOccupancyRate(@Param("buildingNo") String buildingNo);

    // ==================== 更新（乐观锁） ====================

    /**
     * 增加宿舍已入住人数（乐观锁）
     */
    @Update("UPDATE dormitories SET occupied = occupied + 1, version = version + 1 WHERE id = #{dormId} AND occupied < capacity AND version = #{version}")
    int incrementOccupied(@Param("dormId") Long dormId, @Param("version") Integer version);

    /**
     * 减少宿舍已入住人数（乐观锁）
     */
    @Update("UPDATE dormitories SET occupied = occupied - 1, version = version + 1 WHERE id = #{dormId} AND occupied > 0 AND version = #{version}")
    int decrementOccupied(@Param("dormId") Long dormId, @Param("version") Integer version);

    /**
     * 自动更新宿舍状态（根据入住人数）
     */
    @Update("UPDATE dormitories SET status = CASE WHEN occupied >= capacity THEN 'full' ELSE 'available' END WHERE id = #{dormId}")
    int autoUpdateStatus(@Param("dormId") Long dormId);
}