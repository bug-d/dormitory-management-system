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

    /**
     * 查询所有可用宿舍（状态为 available 且未满）
     *
     * @return 可用宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE status = 'available' AND occupied < capacity ORDER BY building_no, room_no")
    List<Dormitory> selectAvailableDorms();

    /**
     * 根据性别查询可用宿舍
     *
     * @param gender 性别（M-男，F-女）
     * @return 可用宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE gender = #{gender} AND status = 'available' AND occupied < capacity ORDER BY building_no, room_no")
    List<Dormitory> selectAvailableDormsByGender(@Param("gender") String gender);

    /**
     * 根据楼栋查询宿舍
     *
     * @param buildingNo 楼栋号
     * @return 宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE building_no = #{buildingNo} ORDER BY room_no")
    List<Dormitory> selectByBuilding(@Param("buildingNo") String buildingNo);

    /**
     * 根据状态查询宿舍
     *
     * @param status 状态
     * @return 宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE status = #{status} ORDER BY building_no, room_no")
    List<Dormitory> selectByStatus(@Param("status") String status);

    /**
     * 根据性别查询宿舍
     *
     * @param gender 性别
     * @return 宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE gender = #{gender} ORDER BY building_no, room_no")
    List<Dormitory> selectByGender(@Param("gender") String gender);

    /**
     * 查询所有宿舍楼栋列表
     *
     * @return 楼栋号列表
     */
    @Select("SELECT DISTINCT building_no FROM dormitories ORDER BY building_no")
    List<String> selectAllBuildings();

    /**
     * 查询已满的宿舍
     *
     * @return 已满宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE occupied >= capacity AND status != 'closed' ORDER BY building_no, room_no")
    List<Dormitory> selectFullDorms();

    /**
     * 查询空宿舍（未入住）
     *
     * @return 空宿舍列表
     */
    @Select("SELECT * FROM dormitories WHERE occupied = 0 AND status = 'available' ORDER BY building_no, room_no")
    List<Dormitory> selectEmptyDorms();

    /**
     * 统计各楼栋宿舍数
     *
     * @return 楼栋统计数据
     */
    @Select("SELECT building_no, COUNT(*) as total, SUM(CASE WHEN status = 'available' THEN 1 ELSE 0 END) as available, SUM(CASE WHEN occupied >= capacity THEN 1 ELSE 0 END) as full FROM dormitories GROUP BY building_no")
    List<Map<String, Object>> selectBuildingStatistics();

    /**
     * 统计总床位数、已入住数、空床位数
     *
     * @return 统计数据
     */
    @Select("SELECT SUM(capacity) as total_beds, SUM(occupied) as occupied_beds, SUM(capacity - occupied) as empty_beds FROM dormitories WHERE status != 'closed'")
    Map<String, Object> selectOverallStatistics();

    /**
     * 增加宿舍已入住人数（乐观锁）
     *
     * @param dormId  宿舍ID
     * @param version 版本号
     * @return 影响行数
     */
    @Update("UPDATE dormitories SET occupied = occupied + 1, version = version + 1 WHERE id = #{dormId} AND occupied < capacity AND version = #{version}")
    int incrementOccupied(@Param("dormId") Long dormId, @Param("version") Integer version);

    /**
     * 减少宿舍已入住人数（乐观锁）
     *
     * @param dormId  宿舍ID
     * @param version 版本号
     * @return 影响行数
     */
    @Update("UPDATE dormitories SET occupied = occupied - 1, version = version + 1 WHERE id = #{dormId} AND occupied > 0 AND version = #{version}")
    int decrementOccupied(@Param("dormId") Long dormId, @Param("version") Integer version);

    /**
     * 更新宿舍状态（根据入住人数自动更新）
     *
     * @param dormId 宿舍ID
     * @return 影响行数
     */
    @Update("UPDATE dormitories SET status = CASE WHEN occupied >= capacity THEN 'full' ELSE 'available' END WHERE id = #{dormId}")
    int autoUpdateStatus(@Param("dormId") Long dormId);

    /**
     * 统计某性别的总床位数和已入住数
     *
     * @param gender 性别
     * @return 统计数据
     */
    @Select("SELECT SUM(capacity) as total_beds, SUM(occupied) as occupied_beds FROM dormitories WHERE gender = #{gender} AND status != 'closed'")
    Map<String, Object> selectStatisticsByGender(@Param("gender") String gender);

    /**
     * 查询某楼栋的宿舍入住率统计
     *
     * @param buildingNo 楼栋号
     * @return 入住率数据
     */
    @Select("SELECT building_no, COUNT(*) as dorm_count, SUM(capacity) as total_beds, SUM(occupied) as occupied_beds, ROUND(SUM(occupied) * 100.0 / SUM(capacity), 2) as occupancy_rate FROM dormitories WHERE building_no = #{buildingNo} GROUP BY building_no")
    Map<String, Object> selectBuildingOccupancyRate(@Param("buildingNo") String buildingNo);
}