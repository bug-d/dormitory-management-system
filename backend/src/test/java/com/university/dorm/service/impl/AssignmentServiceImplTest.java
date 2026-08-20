package com.university.dorm.service.impl;

import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.Student;
import com.university.dorm.mapper.AssignmentMapper;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentMapper assignmentMapper;

    @Mock
    private DormitoryMapper dormitoryMapper;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Student testStudent;
    private Dormitory testDorm;
    private DormAssignment testAssignment;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("测试学生");
        testStudent.setGender("M");

        testDorm = new Dormitory();
        testDorm.setId(1L);
        testDorm.setBuildingNo("1栋");
        testDorm.setRoomNo("101");
        testDorm.setGender("M");
        testDorm.setCapacity(4);
        testDorm.setOccupied(2);
        testDorm.setStatus("available");
        testDorm.setVersion(0);

        testAssignment = new DormAssignment();
        testAssignment.setId(1L);
        testAssignment.setStudentId(1L);
        testAssignment.setDormId(1L);
        testAssignment.setBedNo("A");
        testAssignment.setStatus("active");
    }

    // ==================== 测试方法 ====================

    @Test
    void testSelectActiveAssignmentByDormAndBed() {
        // 测试根据宿舍ID和床号查询活跃入住记录
        when(assignmentMapper.selectActiveAssignmentByDormAndBed(anyLong(), anyString()))
                .thenReturn(testAssignment);

        DormAssignment result = assignmentMapper.selectActiveAssignmentByDormAndBed(1L, "A");

        assert result != null;
        assert result.getId().equals(1L);
        assert result.getBedNo().equals("A");
    }

    @Test
    void testSelectActiveByStudentId() {
        when(assignmentMapper.selectActiveByStudentId(anyLong()))
                .thenReturn(testAssignment);

        DormAssignment result = assignmentMapper.selectActiveByStudentId(1L);

        assert result != null;
        assert result.getStudentId().equals(1L);
    }

    @Test
    void testHasActiveAssignment() {
        when(assignmentMapper.hasActiveAssignment(anyLong()))
                .thenReturn(true);

        boolean result = assignmentMapper.hasActiveAssignment(1L);

        assert result == true;
    }

    @Test
    void testCountAllActive() {
        when(assignmentMapper.countAllActive())
                .thenReturn(10L);

        Long count = assignmentMapper.countAllActive();

        assert count == 10L;
    }
}