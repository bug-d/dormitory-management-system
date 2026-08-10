package com.university.dorm.controller.common;

import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.Student;
import com.university.dorm.service.DormitoryService;
import com.university.dorm.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DormitoryService dormitoryService;
    private final StudentService studentService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        List<Dormitory> dorms = dormitoryService.listAll().stream()
                .filter(item -> !"closed".equals(item.getStatus()))
                .toList();
        int totalBeds = dorms.stream().mapToInt(Dormitory::getCapacity).sum();
        int occupiedBeds = dorms.stream().mapToInt(Dormitory::getOccupied).sum();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBeds", totalBeds);
        stats.put("occupiedBeds", occupiedBeds);
        stats.put("emptyBeds", Math.max(totalBeds - occupiedBeds, 0));
        stats.put("occupancyRate", totalBeds == 0 ? 0D : Math.round(occupiedBeds * 1000D / totalBeds) / 10D);
        return Result.success(stats);
    }

    @GetMapping("/chart/building-occupancy")
    public Result<List<Map<String, Object>>> getBuildingOccupancy(
            @RequestParam(required = false) String gender) {
        Map<String, List<Dormitory>> groups = dormitoryService.listAll().stream()
                .filter(item -> gender == null || gender.isBlank() || gender.equals(item.getGender()))
                .collect(Collectors.groupingBy(Dormitory::getBuildingNo, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> result = new ArrayList<>();
        groups.forEach((building, dorms) -> {
            int capacity = dorms.stream().mapToInt(Dormitory::getCapacity).sum();
            int occupied = dorms.stream().mapToInt(Dormitory::getOccupied).sum();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", building);
            item.put("value", capacity == 0 ? 0D : Math.round(occupied * 1000D / capacity) / 10D);
            result.add(item);
        });
        return Result.success(result);
    }

    @GetMapping("/chart/gender-ratio")
    public Result<List<Map<String, Object>>> getGenderRatio() {
        List<Student> students = studentService.getActiveStudents();
        long male = students.stream().filter(item -> "M".equals(item.getGender())).count();
        long female = students.stream().filter(item -> "F".equals(item.getGender())).count();
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(chartItem("男生", male, "#409EFF"));
        result.add(chartItem("女生", female, "#F56C6C"));
        return Result.success(result);
    }

    @GetMapping("/activities")
    public Result<List<Map<String, Object>>> getActivities() {
        return Result.success(List.of());
    }

    private Map<String, Object> chartItem(String name, long value, String color) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("value", value);
        item.put("color", color);
        return item;
    }
}

