package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.service.DormitoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DormitoryServiceImpl implements DormitoryService {

    @Autowired
    private DormitoryMapper dormitoryMapper;

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
    public Page<Dormitory> pageQuery(Integer pageNum, Integer pageSize, String buildingNo, String gender, String status, String orderBy, String orderDir) {
        Page<Dormitory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();

        // ========== 搜索条件 ==========
        if (buildingNo != null && !buildingNo.isEmpty()) {
            wrapper.eq(Dormitory::getBuildingNo, buildingNo);
        }
        if (gender != null && !gender.isEmpty()) {
            wrapper.eq(Dormitory::getGender, gender);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Dormitory::getStatus, status);
        }

        // ========== 排序（跨页排序） ==========
        if (orderBy != null && !orderBy.isEmpty()) {
            boolean isAsc = "asc".equalsIgnoreCase(orderDir);
            switch (orderBy) {
                case "id":
                    wrapper.orderBy(true, isAsc, Dormitory::getId);
                    break;
                case "buildingNo":
                    wrapper.orderBy(true, isAsc, Dormitory::getBuildingNo);
                    break;
                case "floorNo":
                    wrapper.orderBy(true, isAsc, Dormitory::getFloorNo);
                    break;
                case "roomNo":
                    wrapper.orderBy(true, isAsc, Dormitory::getRoomNo);
                    break;
                default:
                    wrapper.orderBy(true, isAsc, Dormitory::getId);
                    break;
            }
        } else {
            // 默认按 ID 升序
            wrapper.orderByAsc(Dormitory::getId);
        }

        return dormitoryMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(DormRequest request) {
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
        dorm.setStatus(request.getStatus() != null ? request.getStatus() : StatusConstant.DORM_AVAILABLE);
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
        existing.setStatus(request.getStatus());
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
        if (dorm.getOccupied() > 0) {
            throw new BusinessException("该宿舍已有人入住，不能删除");
        }
        dormitoryMapper.deleteById(id);
        log.info("删除宿舍成功: {}-{}", dorm.getBuildingNo(), dorm.getRoomNo());
    }

    private boolean existsByBuildingAndRoom(String buildingNo, String roomNo) {
        return dormitoryMapper.selectCount(
            new LambdaQueryWrapper<Dormitory>()
                .eq(Dormitory::getBuildingNo, buildingNo)
                .eq(Dormitory::getRoomNo, roomNo)
        ) > 0;
    }

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
    public List<String> getAllBuildings() {
        return dormitoryMapper.selectAllBuildings();
    }

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Long totalBeds = dormitoryMapper.selectCount(null) * 4L;
        Long occupiedBeds = 0L;
        Long emptyBeds = totalBeds - occupiedBeds;
        String occupancyRate = totalBeds > 0 ? 
            String.format("%.1f", (occupiedBeds * 100.0) / totalBeds) : "0";
        stats.put("totalBeds", totalBeds);
        stats.put("occupiedBeds", occupiedBeds);
        stats.put("emptyBeds", emptyBeds > 0 ? emptyBeds : 0);
        stats.put("occupancyRate", occupancyRate);
        return stats;
    }

    @Override
    public boolean incrementOccupied(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.isFull()) {
            return false;
        }
        dorm.setOccupied(dorm.getOccupied() + 1);
        dormitoryMapper.updateById(dorm);
        return true;
    }

    @Override
    public boolean decrementOccupied(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.getOccupied() <= 0) {
            return false;
        }
        dorm.setOccupied(dorm.getOccupied() - 1);
        dormitoryMapper.updateById(dorm);
        return true;
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
}