package com.teachera.service;

import com.teachera.model.Leave;
import com.teachera.model.LeaveStatus;
import com.teachera.model.LeaveType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveService {

    Leave requestLeave(int teacherId,
            LocalDate startDate,
            LocalDate endDate,
            LeaveType type,
            String reason);

    Leave approveLeave(int leaveId, int reviewerUserId);

    Leave rejectLeave(int leaveId, int reviewerUserId, String reason);

    Optional<Leave> getLeaveById(int id);

    List<Leave> getLeavesForTeacher(int teacherId);

    List<Leave> getLeavesByStatus(LeaveStatus status);

    List<Leave> getAllLeaves();
}
