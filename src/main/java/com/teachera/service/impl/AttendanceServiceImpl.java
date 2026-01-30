package com.teachera.service.impl;

import com.teachera.dao.AttendanceDAO;
import com.teachera.dao.TeacherDAO;
import com.teachera.model.Attendance;
import com.teachera.model.AttendanceStatus;
import com.teachera.model.Teacher;
import com.teachera.service.AttendanceService;
import com.teachera.service.ServiceException;
import com.teachera.service.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private final TeacherDAO teacherDAO;

    public AttendanceServiceImpl(AttendanceDAO attendanceDAO, TeacherDAO teacherDAO) {
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO must not be null");
        this.teacherDAO = Objects.requireNonNull(teacherDAO, "teacherDAO must not be null");
    }

    @Override
    public Attendance recordAttendance(int teacherId,
                                       LocalDate date,
                                       AttendanceStatus status,
                                       String hoursWorkedText,
                                       String remarks) {

        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new ServiceException("Teacher not found with id " + teacherId));

        ValidationUtils.requireNotNull(date, "Attendance date is required.");
        ValidationUtils.requireNotNull(status, "Attendance status is required.");

        // Prevent duplicate attendance for same day
        attendanceDAO.findByTeacherAndDate(teacher.getId(), date)
                .ifPresent(a -> {
                    throw new ServiceException("Attendance already recorded for this teacher on " + date + ".");
                });
        BigDecimal hoursWorked = ValidationUtils.parseNonNegativeBigDecimal(hoursWorkedText, "hoursWorked");

        Attendance attendance = new Attendance(null, teacher.getId(), date, status, hoursWorked, remarks, null);

        attendanceDAO.save(attendance);
        return attendance;
    }

    @Override
    public Attendance updateAttendance(Attendance attendance) {
        if (attendance == null || attendance.getId() == null) {
            throw new ServiceException("Attendance ID is required for update.");
        }
        if (attendance.getAttendanceDate() == null) {
            throw new ServiceException("Attendance date is required.");
        }
        if (attendance.getStatus() == null) {
            throw new ServiceException("Attendance status is required.");
        }

        if (attendance.getHoursWorked() != null && attendance.getHoursWorked().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("Hours worked cannot be negative.");
        }

        attendanceDAO.update(attendance);
        return attendance;
    }

    @Override
    public Optional<Attendance> getAttendanceById(int id) {
        return attendanceDAO.findById(id);
    }

    @Override
    public Optional<Attendance> getAttendanceForTeacherOnDate(int teacherId, LocalDate date) {
        return attendanceDAO.findByTeacherAndDate(teacherId, date);
    }

    @Override
    public List<Attendance> getAttendanceForTeacher(int teacherId) {
        return attendanceDAO.findByTeacher(teacherId);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceDAO.findAll();
    }

}

