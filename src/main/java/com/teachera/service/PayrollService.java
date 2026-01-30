package com.teachera.service;

import com.teachera.model.Payroll;

import java.util.List;
import java.util.Optional;

public interface PayrollService {

    Payroll generatePayrollForTeacher(int teacherId, int year, int month);

    Optional<Payroll> getPayrollById(int id);

    Optional<Payroll> getPayrollForTeacherAndPeriod(int teacherId, int year, int month);

    List<Payroll> getPayrollForTeacher(int teacherId);

    List<Payroll> getAllPayroll();
}
