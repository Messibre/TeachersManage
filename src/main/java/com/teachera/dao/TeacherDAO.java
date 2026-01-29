package com.teachera.dao;

import com.teachera.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherDAO {

    void save(Teacher teacher);

    void update(Teacher teacher);

    void deleteById(int id);

    Optional<Teacher> findById(int id);

    Optional<Teacher> findByTeacherCode(String teacherCode);

    List<Teacher> findAll();
}
