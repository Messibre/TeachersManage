package com.teachera.service.impl;

import com.teachera.dao.LeaveDAO;
import com.teachera.dao.TeacherDAO;
import com.teachera.model.Leave;
import com.teachera.model.LeaveStatus;
import com.teachera.model.LeaveType;
import com.teachera.model.Teacher;
import com.teachera.service.LeaveService;
import com.teachera.service.ServiceException;
import com.teachera.service.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LeaveServiceImpl implements LeaveService {

    private final LeaveDAO leaveDAO;
    private final TeacherDAO teacherDAO;

    public LeaveServiceImpl(LeaveDAO leaveDAO, TeacherDAO teacherDAO) {
        this.leaveDAO = Objects.requireNonNull(leaveDAO, "leaveDAO must not be null");
        this.teacherDAO = Objects.requireNonNull(teacherDAO, "teacherDAO must not be null");
    }

    @Override
    public Leave requestLeave(int teacherId,
                              LocalDate startDate,
                              LocalDate endDate,
                              LeaveType type,
                              String reason) {

        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new ServiceException("Teacher not found with id " + teacherId));

        ValidationUtils.requireNotNull(startDate, "Start date is required.");
        ValidationUtils.requireNotNull(endDate, "End date is required.");
        ValidationUtils.requireDateRange(startDate, endDate, "Invalid leave date range.");
        ValidationUtils.requireNotNull(type, "Leave type is required.");

        Leave leave = new Leave(
                null,
                teacher.getId(),
                startDate,
                endDate,
                type,
                LeaveStatus.PENDING,
                reason,
                LocalDateTime.now(),
                null,
                null
        );

        leaveDAO.save(leave);
        return leave;
    }

    @Override
    public Leave approveLeave(int leaveId, int reviewerUserId) {
        Leave leave = getExistingPendingLeave(leaveId);

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setReviewedByUserId(reviewerUserId);
        leave.setReviewedAt(LocalDateTime.now());

        leaveDAO.update(leave);
        return leave;
    }

    @Override
    public Leave rejectLeave(int leaveId, int reviewerUserId, String reason) {
        Leave leave = getExistingPendingLeave(leaveId);

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setReviewedByUserId(reviewerUserId);
        leave.setReviewedAt(LocalDateTime.now());

        if (reason != null && !reason.trim().isEmpty()) {
            leave.setReason(reason);
        }

        leaveDAO.update(leave);
        return leave;
    }

    @Override
    public Optional<Leave> getLeaveById(int id) {
        return leaveDAO.findById(id);
    }

    @Override
    public List<Leave> getLeavesForTeacher(int teacherId) {
        return leaveDAO.findByTeacher(teacherId);
    }

    @Override
    public List<Leave> getLeavesByStatus(LeaveStatus status) {
        return leaveDAO.findByStatus(status);
    }

    @Override
    public List<Leave> getAllLeaves() {
        return leaveDAO.findAll();
    }

    private Leave getExistingPendingLeave(int leaveId) {
        Leave leave = leaveDAO.findById(leaveId)
                .orElseThrow(() -> new ServiceException("Leave not found with id " + leaveId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ServiceException("Only pending leaves can be approved or rejected.");
        }
        return leave;
    }
}

