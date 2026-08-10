package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.ManagerPermissionMapper;
import com.university.dorm.service.DormitoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 宿舍服务实现类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/impl/DormitoryServiceImpl.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DormitoryServiceImpl implements DormitoryService {

    private final DormitoryMapper dormitoryMapper;
    private final ManagerPermissionMapper permissionMapper;

    // ==================== 基础 CRUD ====================

    @Override
    public Dormitory getById(Long id) {
        return dormitoryMapper.selectById(id);
    }

    @Override
    public Dormitory getByBuildingAndRoom(String buildingNo, String roomNo) {
        return dormitoryMapper.selectOne(
            new LambdaQueryWrapper<Dormitory>()
                .eq(Dormitory::getBuildingNo, buildingNo)
                .eq(Dormitory::getRoomNo, roomNo)
        );
    }

    @Override
    public List<Dormitory> listAll() {
        return dormitoryMapper.selectList(null);
    }

    @Override
    public Page<Dormitory> pageQuery(Integer pageNum, Integer pageSize, String buildingNo, String gender, String status) {
        Page<Dormitory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();

        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.eq(Dormitory::getBuildingNo, buildingNo);
        }
        if (gender != null && !gender.isEmpty()) {
            wrapper.eq(Dormitory::getGender, gender);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Dormitory::getStatus, status);
        }

        wrapper.orderByAsc(Dormitory::getBuildingNo)
               .orderByAsc(Dormitory::getRoomNo);

        return dormitoryMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(DormRequest request) {
        // 检查宿舍是否已存在
        if (existsByBuildingAndRoom(request.getBuildingNo(), request.getRoomNo())) {
            throw new BusinessException("宿舍 " + request.getBuildingNo() + "-" + request.getRoomNo() + " 已存在");
        }

        Dormitory dorm = new Dormitory();
        dorm.setBuildingNo(request.getBuildingNo());
        dorm.setFloorNo(request.getFloorNo());
        dorm.setRoomNo(request.getRoomNo());
        dorm.setGender(request.getGender());
        dorm.setCapacity(request.getCapacity());
        dorm.setOccupied(0);
        dorm.setRoomType(request.getRoomType());
        dorm.setHasAirConditioner(request.getHasAirConditioner());
        dorm.setHasPrivateBathroom(request.getHasPrivateBathroom());
        dorm.setPricePerTerm(request.getPricePerTerm());
        dorm.setStatus(StatusConstant.DORM_AVAILABLE);
        dorm.setDescription(request.getDescription());

        dormitoryMapper.insert(dorm);
        log.info("新增宿舍成功: {}-{}", request.getBuildingNo(), request.getRoomNo());
    }

    @Override
    @Transactional
    public void update(DormRequest request) {
        Dormitory existing = dormitoryMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException("宿舍不存在");
        }

        // 如果房间号变更，检查是否重复
        if (!existing.getRoomNo().equals(request.getRoomNo())) {
            if (existsByBuildingAndRoom(request.getBuildingNo(), request.getRoomNo())) {
                throw new BusinessException("宿舍 " + request.getBuildingNo() + "-" + request.getRoomNo() + " 已存在");
            }
        }

        // 如果要减少容量，检查是否小于已入住人数
        if (request.getCapacity() < existing.getOccupied()) {
            throw new BusinessException("容量不能小于已入住人数（当前已入住 " + existing.getOccupied() + " 人）");
        }

        existing.setBuildingNo(request.getBuildingNo());
        existing.setFloorNo(request.getFloorNo());
        existing.setRoomNo(request.getRoomNo());
        existing.setGender(request.getGender());
        existing.setCapacity(request.getCapacity());
        existing.setRoomType(request.getRoomType());
        existing.setHasAirConditioner(request.getHasAirConditioner());
        existing.setHasPrivateBathroom(request.getHasPrivateBathroom());
        existing.setPricePerTerm(request.getPricePerTerm());
        existing.setDescription(request.getDescription());

        dormitoryMapper.updateById(existing);
        log.info("更新宿舍成功: {}-{}", request.getBuildingNo(), request.getRoomNo());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Dormitory dorm = dormitoryMapper.selectById(id);
        if (dorm == null) {
            throw new BusinessException("宿舍不存在");
        }

        // 检查宿舍是否有人入住
        if (dorm.getOccupied() > 0) {
            throw new BusinessException("该宿舍已有人入住，不能删除");
        }

        // 删除权限关联
        permissionMapper.deleteByDormId(id);
        dormitoryMapper.deleteById(id);
        log.info("删除宿舍成功: {}-{}", dorm.getBuildingNo(), dorm.getRoomNo());
    }

    // ==================== 查询 ====================

    @Override
    public List<Dormitory> getAvailableDorms() {
        return dormitoryMapper.selectAvailableDorms();
    }

    @Override
    public List<Dormitory> getAvailableDormsByGender(String gender) {
        return dormitoryMapper.selectAvailableDormsByGender(gender);
    }

    @Override
    public List<Dormitory> getByBuilding(String buildingNo) {
        return dormitoryMapper.selectByBuilding(buildingNo);
    }

    @Override
    public List<Dormitory> getByStatus(String status) {
        return dormitoryMapper.selectByStatus(status);
    }

    @Override
    public List<String> getAllBuildings() {
        return dormitoryMapper.selectAllBuildings();
    }

    @Override
    public List<Dormitory> getFullDorms() {
        return dormitoryMapper.selectFullDorms();
    }

    @Override
    public List<Dormitory> getEmptyDorms() {
        return dormitoryMapper.selectEmptyDorms();
    }

    @Override
    public List<Dormitory> getManagedDorms(Long managerId) {
        List<Long> dormIds = permissionMapper.selectDormIdsByManagerId(managerId);
        if (dormIds.isEmpty()) {
            return new ArrayList<>();
        }
        return dormitoryMapper.selectBatchIds(dormIds);
    }

    // ==================== 床位管理 ====================

    @Override
    @Transactional
    public boolean incrementOccupied(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.isFull()) {
            return false;
        }

        int updated = dormitoryMapper.incrementOccupied(dormId, dorm.getVersion());
        if (updated > 0) {
            // 自动更新状态
            dormitoryMapper.autoUpdateStatus(dormId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean decrementOccupied(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.getOccupied() <= 0) {
            return false;
        }

        int updated = dormitoryMapper.decrementOccupied(dormId, dorm.getVersion());
        if (updated > 0) {
            dormitoryMapper.autoUpdateStatus(dormId);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasEmptyBed(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        return dorm != null && !dorm.isFull();
    }

    @Override
    public Integer getRemainingCapacity(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        return dorm != null ? dorm.getRemainingCapacity() : 0;
    }

    @Override
    public void autoUpdateStatus(Long dormId) {
        dormitoryMapper.autoUpdateStatus(dormId);
    }

    // ==================== 统计 ====================

    @Override
    public Map<String, Object> getOverallStatistics() {
        return dormitoryMapper.selectOverallStatistics();
    }

    @Override
    public List<Map<String, Object>> getBuildingStatistics() {
        return dormitoryMapper.selectBuildingStatistics();
    }

    @Override
    public Map<String, Object> getStatisticsByGender(String gender) {
        return dormitoryMapper.selectStatisticsByGender(gender);
    }

    @Override
    public Map<String, Object> getBuildingOccupancyRate(String buildingNo) {
        return dormitoryMapper.selectBuildingOccupancyRate(buildingNo);
    }

    @Override
    public Double calculateOccupancyRate(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.getCapacity() == 0) {
            return 0.0;
        }
        return (double) dorm.getOccupied() / dorm.getCapacity() * 100;
    }

    // ==================== 验证 ====================

    @Override
    public boolean isFull(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        return dorm != null && dorm.isFull();
    }

    @Override
    public boolean isAvailable(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        return dorm != null && dorm.isAvailable();
    }

    @Override
    public boolean existsByBuildingAndRoom(String buildingNo, String roomNo) {
        return dormitoryMapper.selectCount(
            new LambdaQueryWrapper<Dormitory>()
                .eq(Dormitory::getBuildingNo, buildingNo)
                .eq(Dormitory::getRoomNo, roomNo)
        ) > 0;
    }
}