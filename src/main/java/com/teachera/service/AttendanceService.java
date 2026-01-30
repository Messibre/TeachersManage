package com.teachera.service;

import com.teachera.model.Attendance;
import com.teachera.model.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Attendance recordAttendance(int teacherId,
            LocalDate date,
            AttendanceStatus status,
            String hoursWorkedText,
            String remarks);

    Attendance updateAttendance(Attendance attendance);

    Optional<Attendance> getAttendanceById(int id);

    Optional<Attendance> getAttendanceForTeacherOnDate(int teacherId, LocalDate date);

    List<Attendance> getAttendanceForTeacher(int teacherId);

    List<Attendance> getAllAttendance();
}
