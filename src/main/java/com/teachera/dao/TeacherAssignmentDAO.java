package com.teachera.dao;

import com.teachera.model.TeacherAssignment;

import java.util.List;
import java.util.Optional;

public interface TeacherAssignmentDAO {
    void save(TeacherAssignment a);
    List<TeacherAssignment> findByTeacherId(int teacherId);
    void deleteById(int id);
    Optional<TeacherAssignment> findById(int id);
}
