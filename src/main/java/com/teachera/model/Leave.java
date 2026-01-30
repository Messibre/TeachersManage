package com.teachera.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Leave {

    private Integer id;
    private Integer teacherId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private LeaveStatus status;
    private String reason;
    private LocalDateTime requestedAt;
    private Integer reviewedByUserId;
    private LocalDateTime reviewedAt;

    public Leave() {
    }

    public Leave(Integer id,
            Integer teacherId,
            LocalDate startDate,
            LocalDate endDate,
            LeaveType leaveType,
            LeaveStatus status,
            String reason,
            LocalDateTime requestedAt,
            Integer reviewedByUserId,
            LocalDateTime reviewedAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.status = status;
        this.reason = reason;
        this.requestedAt = requestedAt;
        this.reviewedByUserId = reviewedByUserId;
        this.reviewedAt = reviewedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Integer getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(Integer reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
