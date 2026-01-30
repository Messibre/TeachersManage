package com.teachera.dao;

import com.teachera.model.Payroll;

import java.util.List;
import java.util.Optional;

public interface PayrollDAO {

    void save(Payroll payroll);

    void update(Payroll payroll);

    void deleteById(int id);

    Optional<Payroll> findById(int id);

    Optional<Payroll> findByTeacherAndPeriod(int teacherId, int year, int month);

    List<Payroll> findByTeacher(int teacherId);

    List<Payroll> findAll();
}
