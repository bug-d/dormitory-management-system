package com.university.dorm.controller.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.service.AssignmentService;
import com.university.dorm.service.DormitoryService;
import com.university.dorm.service.PermissionService;
import com.university.dorm.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager/dorms")
@RequiredArgsConstructor
public class ManagerDormController {

    private final DormitoryService dormitoryService;
    private final AssignmentService assignmentService;
    private final PermissionService permissionService;
    private final SecurityUtil securityUtil;

    @GetMapping("/page")
    public Result<Page<Dormitory>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String buildingNo,
            @RequestParam(required = false) String status) {
        List<Dormitory> filtered = managedDorms().stream()
                .filter(item -> buildingNo == null || buildingNo.isBlank() || buildingNo.equals(item.getBuildingNo()))
                .filter(item -> status == null || status.isBlank() || status.equals(item.getStatus()))
                .collect(Collectors.toList());
        int from = (int) Math.min((pageNum - 1) * pageSize, filtered.size());
        int to = (int) Math.min(from + pageSize, filtered.size());
        Page<Dormitory> page = new Page<>(pageNum, pageSize, filtered.size());
        page.setRecords(filtered.subList(from, to));
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<Dormitory>> list() {
        return Result.success(managedDorms());
    }

    @GetMapping("/{id}")
    public Result<Dormitory> getById(@PathVariable Long id) {
        permissionService.checkPermission(securityUtil.getCurrentUserId(), id);
        return Result.success(dormitoryService.getById(id));
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid DormRequest request) {
        permissionService.checkFullPermission(securityUtil.getCurrentUserId(), request.getId());
        dormitoryService.update(request);
        return Result.success();
    }

    @GetMapping("/stats/overall")
    public Result<Map<String, Object>> overallStats() {
        List<Dormitory> dorms = managedDorms();
        int totalBeds = dorms.stream().mapToInt(Dormitory::getCapacity).sum();
        int occupiedBeds = dorms.stream().mapToInt(Dormitory::getOccupied).sum();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dormCount", dorms.size());
        result.put("totalBeds", totalBeds);
        result.put("occupiedBeds", occupiedBeds);
        result.put("emptyBeds", totalBeds - occupiedBeds);
        result.put("occupancyRate", totalBeds == 0 ? 0D : occupiedBeds * 100D / totalBeds);
        return Result.success(result);
    }

    @GetMapping("/stats/buildings")
    public Result<List<Map<String, Object>>> buildingStats() {
        Map<String, List<Dormitory>> groups = managedDorms().stream()
                .collect(Collectors.groupingBy(Dormitory::getBuildingNo, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        groups.forEach((building, dorms) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            int totalBeds = dorms.stream().mapToInt(Dormitory::getCapacity).sum();
            int occupiedBeds = dorms.stream().mapToInt(Dormitory::getOccupied).sum();
            item.put("buildingNo", building);
            item.put("dormCount", dorms.size());
            item.put("totalBeds", totalBeds);
            item.put("occupiedBeds", occupiedBeds);
            item.put("occupancyRate", totalBeds == 0 ? 0D : occupiedBeds * 100D / totalBeds);
            result.add(item);
        });
        return Result.success(result);
    }

    @GetMapping("/{id}/residents")
    public Result<List<Map<String, Object>>> residents(@PathVariable Long id) {
        permissionService.checkPermission(securityUtil.getCurrentUserId(), id);
        return Result.success(assignmentService.getDormResidents(id));
    }

    @GetMapping("/{id}/history")
    public Result<List<DormAssignment>> history(@PathVariable Long id) {
        permissionService.checkPermission(securityUtil.getCurrentUserId(), id);
        return Result.success(assignmentService.getHistoryByDormId(id));
    }

    private List<Dormitory> managedDorms() {
        return permissionService.getManagedDorms(securityUtil.getCurrentUserId());
    }
}

