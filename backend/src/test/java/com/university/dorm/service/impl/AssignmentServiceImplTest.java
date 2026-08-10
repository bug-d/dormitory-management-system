package com.university.dorm.service.impl;

import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.AssignmentRequest;
import com.university.dorm.dto.request.AuditRequest;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.Student;
import com.university.dorm.mapper.AssignmentMapper;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentMapper assignmentMapper;

    @Mock
    private DormitoryMapper dormitoryMapper;

    @Mock
    private StudentMapper studentMapper;

    private AssignmentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AssignmentServiceImpl(assignmentMapper, dormitoryMapper, studentMapper);
    }

    @Test
    void checkinApplicationDoesNotOccupyBedBeforeApproval() {
        when(studentMapper.selectById(1L)).thenReturn(student(1L));
        when(assignmentMapper.hasActiveOrPending(1L)).thenReturn(false);
        when(dormitoryMapper.selectById(2L)).thenReturn(dorm(2L));

        service.applyCheckin(1L, request(2L, "A"));

        ArgumentCaptor<DormAssignment> captor = ArgumentCaptor.forClass(DormAssignment.class);
        verify(assignmentMapper).insert(captor.capture());
        assertEquals(StatusConstant.ASSIGNMENT_PENDING, captor.getValue().getStatus());
        verify(dormitoryMapper, never()).incrementOccupied(any(), any());
        verify(dormitoryMapper, never()).decrementOccupied(any(), any());
    }

    @Test
    void transferApplicationKeepsOldBedAndDoesNotOccupyTargetBed() {
        DormAssignment current = activeAssignment(7L, 1L, 2L, "A");
        when(studentMapper.selectById(1L)).thenReturn(student(1L));
        when(assignmentMapper.selectActiveByStudentId(1L)).thenReturn(current);
        when(assignmentMapper.hasPendingAssignment(1L)).thenReturn(false);
        when(dormitoryMapper.selectById(3L)).thenReturn(dorm(3L));

        service.applyTransfer(1L, request(3L, "B"));

        verify(assignmentMapper).insert(any(DormAssignment.class));
        verify(dormitoryMapper, never()).incrementOccupied(any(), any());
        verify(dormitoryMapper, never()).decrementOccupied(any(), any());
    }

    @Test
    void approvingCheckinOccupiesTargetBedExactlyOnce() {
        DormAssignment pending = pendingAssignment(10L, 1L, 2L, "A", StatusConstant.TYPE_NEW_CHECKIN);
        when(assignmentMapper.selectById(10L)).thenReturn(pending);
        when(assignmentMapper.selectActiveAssignmentIdByBedForUpdate(2L, "A")).thenReturn(null);
        when(dormitoryMapper.selectById(2L)).thenReturn(dorm(2L));
        when(assignmentMapper.approveAssignment(10L, 9L, "同意")).thenReturn(1);
        when(dormitoryMapper.incrementOccupied(2L, 0)).thenReturn(1);
        when(assignmentMapper.activateAssignment(10L)).thenReturn(1);

        service.audit(audit(10L, "approve", "同意"), 9L);

        verify(dormitoryMapper).incrementOccupied(2L, 0);
        verify(dormitoryMapper, never()).decrementOccupied(any(), any());
        verify(assignmentMapper).activateAssignment(10L);
    }

    @Test
    void approvingTransferMovesOccupancyExactlyOnce() {
        DormAssignment pending = pendingAssignment(11L, 1L, 3L, "B", StatusConstant.TYPE_TRANSFER);
        DormAssignment oldActive = activeAssignment(7L, 1L, 2L, "A");
        when(assignmentMapper.selectById(11L)).thenReturn(pending);
        when(assignmentMapper.selectActiveAssignmentIdByBedForUpdate(3L, "B")).thenReturn(null);
        when(dormitoryMapper.selectById(3L)).thenReturn(dorm(3L));
        when(dormitoryMapper.selectById(2L)).thenReturn(dorm(2L));
        when(assignmentMapper.approveAssignment(11L, 9L, "同意调宿")).thenReturn(1);
        when(dormitoryMapper.incrementOccupied(3L, 0)).thenReturn(1);
        when(assignmentMapper.selectActiveByStudentId(1L)).thenReturn(oldActive);
        when(dormitoryMapper.decrementOccupied(2L, 0)).thenReturn(1);
        when(assignmentMapper.activateAssignment(11L)).thenReturn(1);

        service.audit(audit(11L, "approve", "同意调宿"), 9L);

        verify(dormitoryMapper).incrementOccupied(3L, 0);
        verify(dormitoryMapper).decrementOccupied(2L, 0);
        assertEquals(StatusConstant.ASSIGNMENT_LEFT, oldActive.getStatus());
        verify(assignmentMapper).activateAssignment(11L);
    }

    @Test
    void rejectingPendingApplicationDoesNotReleaseUnoccupiedBed() {
        DormAssignment pending = pendingAssignment(12L, 1L, 2L, "A", StatusConstant.TYPE_NEW_CHECKIN);
        when(assignmentMapper.selectById(12L)).thenReturn(pending);
        when(assignmentMapper.rejectAssignment(12L, 9L, "不同意")).thenReturn(1);

        service.audit(audit(12L, "reject", "不同意"), 9L);

        verify(dormitoryMapper, never()).decrementOccupied(any(), any());
        verify(dormitoryMapper, never()).autoUpdateStatus(any());
    }

    @Test
    void cancelingPendingApplicationDoesNotReleaseUnoccupiedBed() {
        DormAssignment pending = pendingAssignment(13L, 1L, 2L, "A", StatusConstant.TYPE_NEW_CHECKIN);
        when(assignmentMapper.selectById(13L)).thenReturn(pending);

        service.cancelApplication(13L, 1L);

        assertEquals(StatusConstant.ASSIGNMENT_CANCELED, pending.getStatus());
        verify(assignmentMapper).updateById(pending);
        verify(dormitoryMapper, never()).decrementOccupied(any(), any());
        verify(dormitoryMapper, never()).autoUpdateStatus(any());
    }

    private Student student(Long id) {
        Student student = new Student();
        student.setId(id);
        student.setGender("M");
        return student;
    }

    private Dormitory dorm(Long id) {
        Dormitory dorm = new Dormitory();
        dorm.setId(id);
        dorm.setGender("M");
        dorm.setCapacity(4);
        dorm.setOccupied(0);
        dorm.setStatus(StatusConstant.DORM_AVAILABLE);
        dorm.setVersion(0);
        return dorm;
    }

    private AssignmentRequest request(Long dormId, String bedNo) {
        AssignmentRequest request = new AssignmentRequest();
        request.setDormId(dormId);
        request.setBedNo(bedNo);
        request.setSemester("2026-2027-1");
        request.setApplyReason("测试");
        return request;
    }

    private AuditRequest audit(Long assignmentId, String action, String remark) {
        AuditRequest request = new AuditRequest();
        request.setAssignmentId(assignmentId);
        request.setAction(action);
        request.setRemark(remark);
        return request;
    }

    private DormAssignment pendingAssignment(Long id, Long studentId, Long dormId, String bedNo, String type) {
        DormAssignment assignment = new DormAssignment();
        assignment.setId(id);
        assignment.setStudentId(studentId);
        assignment.setDormId(dormId);
        assignment.setBedNo(bedNo);
        assignment.setType(type);
        assignment.setStatus(StatusConstant.ASSIGNMENT_PENDING);
        return assignment;
    }

    private DormAssignment activeAssignment(Long id, Long studentId, Long dormId, String bedNo) {
        DormAssignment assignment = new DormAssignment();
        assignment.setId(id);
        assignment.setStudentId(studentId);
        assignment.setDormId(dormId);
        assignment.setBedNo(bedNo);
        assignment.setType(StatusConstant.TYPE_NEW_CHECKIN);
        assignment.setStatus(StatusConstant.ASSIGNMENT_ACTIVE);
        return assignment;
    }
}
