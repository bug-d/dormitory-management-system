package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.entity.Dormitory;

import java.util.List;
import java.util.Map;

public interface DormitoryService {

    Dormitory getById(Long id);

    Dormitory getByBuildingAndRoom(String buildingNo, String roomNo);

    List<Dormitory> listAll();

    /**
     * 分页查询宿舍（支持排序）
     */
    Page<Dormitory> pageQuery(Integer pageNum, Integer pageSize, String buildingNo, String gender, String status, String orderBy, String orderDir);

    void add(DormRequest request);

    void update(DormRequest request);

    void delete(Long id);

    List<Dormitory> getAvailableDorms();

    List<Dormitory> getAvailableDormsByGender(String gender);

    List<Dormitory> getByBuilding(String buildingNo);

    List<String> getAllBuildings();

    Map<String, Object> getOverallStatistics();

    boolean incrementOccupied(Long dormId);

    boolean decrementOccupied(Long dormId);

    boolean hasEmptyBed(Long dormId);

    Integer getRemainingCapacity(Long dormId);
}