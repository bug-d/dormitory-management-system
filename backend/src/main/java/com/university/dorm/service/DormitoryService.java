package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.entity.Dormitory;

import java.util.List;
import java.util.Map;

/**
 * 宿舍服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/DormitoryService.java
 * 作用：定义宿舍相关的业务方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface DormitoryService {

    // ==================== 基础 CRUD ====================

    /**
     * 根据ID查询宿舍
     *
     * @param id 宿舍ID
     * @return 宿舍对象
     */
    Dormitory getById(Long id);

    /**
     * 根据楼栋和房间号查询宿舍
     *
     * @param buildingNo 楼栋号
     * @param roomNo     房间号
     * @return 宿舍对象
     */
    Dormitory getByBuildingAndRoom(String buildingNo, String roomNo);

    /**
     * 查询所有宿舍
     *
     * @return 宿舍列表
     */
    List<Dormitory> listAll();

    /**
     * 分页查询宿舍
     *
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @param buildingNo 楼栋号（可选）
     * @param gender     性别（可选）
     * @param status     状态（可选）
     * @return 分页结果
     */
    Page<Dormitory> pageQuery(Integer pageNum, Integer pageSize, String buildingNo, String gender, String status);

    /**
     * 新增宿舍
     *
     * @param request 宿舍请求DTO
     */
    void add(DormRequest request);

    /**
     * 更新宿舍
     *
     * @param request 宿舍请求DTO
     */
    void update(DormRequest request);

    /**
     * 删除宿舍
     *
     * @param id 宿舍ID
     */
    void delete(Long id);

    // ==================== 查询 ====================

    /**
     * 查询所有可用宿舍（状态为 available 且未满）
     *
     * @return 可用宿舍列表
     */
    List<Dormitory> getAvailableDorms();

    /**
     * 根据性别查询可用宿舍
     *
     * @param gender 性别
     * @return 可用宿舍列表
     */
    List<Dormitory> getAvailableDormsByGender(String gender);

    /**
     * 根据楼栋查询宿舍
     *
     * @param buildingNo 楼栋号
     * @return 宿舍列表
     */
    List<Dormitory> getByBuilding(String buildingNo);

    /**
     * 根据状态查询宿舍
     *
     * @param status 状态
     * @return 宿舍列表
     */
    List<Dormitory> getByStatus(String status);

    /**
     * 查询所有楼栋号
     *
     * @return 楼栋号列表
     */
    List<String> getAllBuildings();

    /**
     * 查询已满的宿舍
     *
     * @return 已满宿舍列表
     */
    List<Dormitory> getFullDorms();

    /**
     * 查询空宿舍（未入住）
     *
     * @return 空宿舍列表
     */
    List<Dormitory> getEmptyDorms();

    /**
     * 根据管理员ID查询其管辖的宿舍
     *
     * @param managerId 管理员ID
     * @return 宿舍列表
     */
    List<Dormitory> getManagedDorms(Long managerId);

    // ==================== 床位管理 ====================

    /**
     * 增加宿舍已入住人数（入住时调用）
     *
     * @param dormId 宿舍ID
     * @return true-成功，false-失败（已满）
     */
    boolean incrementOccupied(Long dormId);

    /**
     * 减少宿舍已入住人数（退宿时调用）
     *
     * @param dormId 宿舍ID
     * @return true-成功，false-失败
     */
    boolean decrementOccupied(Long dormId);

    /**
     * 检查宿舍是否有空床位
     *
     * @param dormId 宿舍ID
     * @return true-有空位，false-已满
     */
    boolean hasEmptyBed(Long dormId);

    /**
     * 获取宿舍剩余床位数
     *
     * @param dormId 宿舍ID
     * @return 剩余床位数
     */
    Integer getRemainingCapacity(Long dormId);

    /**
     * 自动更新宿舍状态（根据入住人数）
     *
     * @param dormId 宿舍ID
     */
    void autoUpdateStatus(Long dormId);

    // ==================== 统计 ====================

    /**
     * 获取总体统计数据（总床位、已入住、空床位）
     *
     * @return 统计数据
     */
    Map<String, Object> getOverallStatistics();

    /**
     * 获取各楼栋统计
     *
     * @return 楼栋统计数据
     */
    List<Map<String, Object>> getBuildingStatistics();

    /**
     * 获取各性别宿舍统计
     *
     * @param gender 性别
     * @return 统计数据
     */
    Map<String, Object> getStatisticsByGender(String gender);

    /**
     * 获取某楼栋的入住率
     *
     * @param buildingNo 楼栋号
     * @return 入住率数据
     */
    Map<String, Object> getBuildingOccupancyRate(String buildingNo);

    /**
     * 计算某宿舍的入住率
     *
     * @param dormId 宿舍ID
     * @return 入住率（百分比）
     */
    Double calculateOccupancyRate(Long dormId);

    // ==================== 验证 ====================

    /**
     * 检查宿舍是否已满
     *
     * @param dormId 宿舍ID
     * @return true-已满，false-未满
     */
    boolean isFull(Long dormId);

    /**
     * 检查宿舍是否可用
     *
     * @param dormId 宿舍ID
     * @return true-可用，false-不可用
     */
    boolean isAvailable(Long dormId);

    /**
     * 检查宿舍是否存在
     *
     * @param buildingNo 楼栋号
     * @param roomNo     房间号
     * @return true-存在，false-不存在
     */
    boolean existsByBuildingAndRoom(String buildingNo, String roomNo);
}