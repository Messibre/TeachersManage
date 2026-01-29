package com.teachera.dao;

import com.teachera.model.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDAO {

    void save(Attendance attendance);

    void update(Attendance attendance);

    void deleteById(int id);

    Optional<Attendance> findById(int id);

    Optional<Attendance> findByTeacherAndDate(int teacherId, LocalDate date);

    List<Attendance> findByTeacher(int teacherId);

    List<Attendance> findAll();
}
