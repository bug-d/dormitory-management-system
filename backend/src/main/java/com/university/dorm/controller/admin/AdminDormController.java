package com.university.dorm.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.DormRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.service.DormitoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/dorms")
@Tag(name = "宿舍管理", description = "管理员端宿舍管理接口")
public class AdminDormController {

    @Autowired
    private DormitoryService dormitoryService;

    @GetMapping("/page")
    @Operation(summary = "分页查询宿舍")
    public Result<Page<Dormitory>> pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buildingNo,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDir) {
        Page<Dormitory> page = dormitoryService.pageQuery(pageNum, pageSize, buildingNo, gender, status, orderBy, orderDir);
        return Result.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有宿舍")
    public Result<List<Dormitory>> listAll() {
        return Result.success(dormitoryService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询宿舍")
    public Result<Dormitory> getById(@PathVariable Long id) {
        return Result.success(dormitoryService.getById(id));
    }

    @GetMapping("/available")
    @Operation(summary = "查询所有可用宿舍")
    public Result<List<Dormitory>> getAvailableDorms() {
        return Result.success(dormitoryService.getAvailableDorms());
    }

    @GetMapping("/available/{gender}")
    @Operation(summary = "根据性别查询可用宿舍")
    public Result<List<Dormitory>> getAvailableDormsByGender(@PathVariable String gender) {
        return Result.success(dormitoryService.getAvailableDormsByGender(gender));
    }

    @GetMapping("/building/{buildingNo}")
    @Operation(summary = "根据楼栋查询宿舍")
    public Result<List<Dormitory>> getByBuilding(@PathVariable String buildingNo) {
        return Result.success(dormitoryService.getByBuilding(buildingNo));
    }

    @GetMapping("/buildings")
    @Operation(summary = "查询所有楼栋号")
    public Result<List<String>> getAllBuildings() {
        return Result.success(dormitoryService.getAllBuildings());
    }

    @PostMapping
    @Operation(summary = "新增宿舍")
    public Result<Void> add(@RequestBody @Valid DormRequest request) {
        dormitoryService.add(request);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新宿舍")
    public Result<Void> update(@RequestBody @Valid DormRequest request) {
        dormitoryService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除宿舍")
    public Result<Void> delete(@PathVariable Long id) {
        dormitoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/stats/overall")
    @Operation(summary = "获取总体统计数据")
    public Result<Map<String, Object>> getOverallStatistics() {
        return Result.success(dormitoryService.getOverallStatistics());
    }
}