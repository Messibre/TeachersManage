package com.teachera.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {

    private Integer id;
    private Integer teacherId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private BigDecimal hoursWorked;
    private String remarks;
    private LocalDateTime createdAt;

    public Attendance() {
    }

    public Attendance(Integer id,
            Integer teacherId,
            LocalDate attendanceDate,
            AttendanceStatus status,
            BigDecimal hoursWorked,
            String remarks,
            LocalDateTime createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.hoursWorked = hoursWorked;
        this.remarks = remarks;
        this.createdAt = createdAt;
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

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
