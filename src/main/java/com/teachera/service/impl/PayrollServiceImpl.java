package com.teachera.service.impl;

import com.teachera.dao.AttendanceDAO;
import com.teachera.dao.PayrollDAO;
import com.teachera.dao.TeacherDAO;
import com.teachera.model.Attendance;
import com.teachera.model.AttendanceStatus;
import com.teachera.model.Payroll;
import com.teachera.model.Teacher;
import com.teachera.service.PayrollService;
import com.teachera.service.ServiceException;
import com.teachera.service.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PayrollServiceImpl implements PayrollService {

    private final PayrollDAO payrollDAO;
    private final TeacherDAO teacherDAO;
    private final AttendanceDAO attendanceDAO;

    public PayrollServiceImpl(PayrollDAO payrollDAO, TeacherDAO teacherDAO, AttendanceDAO attendanceDAO) {
        this.payrollDAO = Objects.requireNonNull(payrollDAO, "payrollDAO must not be null");
        this.teacherDAO = Objects.requireNonNull(teacherDAO, "teacherDAO must not be null");
        this.attendanceDAO = Objects.requireNonNull(attendanceDAO, "attendanceDAO must not be null");
    }

    @Override
    public Payroll generatePayrollForTeacher(int teacherId, int year, int month) {
        ValidationUtils.requireMonth(month);

        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new ServiceException("Teacher not found with id " + teacherId));

        Optional<Payroll> existing = payrollDAO.findByTeacherAndPeriod(teacherId, year, month);
        if (existing.isPresent()) {
            return existing.get();
        }

        BigDecimal baseSalary = defaultMoney(teacher.getBaseSalary());

        List<Attendance> attendanceList = attendanceDAO.findByTeacher(teacherId);

        long absentDaysThisMonth = attendanceList.stream()
                .filter(a -> isSameYearMonth(a.getAttendanceDate(), year, month))
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        BigDecimal perDay = baseSalary.divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal deductions = perDay.multiply(BigDecimal.valueOf(absentDaysThisMonth));

        BigDecimal overtimeHours = BigDecimal.ZERO;
        BigDecimal overtimeAmount = BigDecimal.ZERO;

        BigDecimal netSalary = baseSalary.subtract(deductions).add(overtimeAmount);

        Payroll payroll = new Payroll(
                null,
                teacherId,
                year,
                month,
                baseSalary,
                overtimeHours,
                overtimeAmount,
                deductions,
                netSalary,
                null);

        payrollDAO.save(payroll);
        return payroll;
    }

    @Override
    public Optional<Payroll> getPayrollById(int id) {
        return payrollDAO.findById(id);
    }

    @Override
    public Optional<Payroll> getPayrollForTeacherAndPeriod(int teacherId, int year, int month) {
        return payrollDAO.findByTeacherAndPeriod(teacherId, year, month);
    }

    @Override
    public List<Payroll> getPayrollForTeacher(int teacherId) {
        return payrollDAO.findByTeacher(teacherId);
    }

    @Override
    public List<Payroll> getAllPayroll() {
        return payrollDAO.findAll();
    }

    private boolean isSameYearMonth(LocalDate date, int year, int month) {
        return date != null && date.getYear() == year && date.getMonthValue() == month;
    }

    private BigDecimal defaultMoney(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
