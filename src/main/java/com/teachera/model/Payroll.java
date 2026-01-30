package com.teachera.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payroll {

    private Integer id;
    private Integer teacherId;
    private int periodYear;
    private int periodMonth;
    private BigDecimal baseSalary;
    private BigDecimal overtimeHours;
    private BigDecimal overtimeAmount;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private LocalDateTime generatedAt;

    public Payroll() {
    }

    public Payroll(Integer id,
            Integer teacherId,
            int periodYear,
            int periodMonth,
            BigDecimal baseSalary,
            BigDecimal overtimeHours,
            BigDecimal overtimeAmount,
            BigDecimal deductions,
            BigDecimal netSalary,
            LocalDateTime generatedAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.baseSalary = baseSalary;
        this.overtimeHours = overtimeHours;
        this.overtimeAmount = overtimeAmount;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.generatedAt = generatedAt;
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

    public int getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(int periodYear) {
        this.periodYear = periodYear;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(int periodMonth) {
        this.periodMonth = periodMonth;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public BigDecimal getOvertimeAmount() {
        return overtimeAmount;
    }

    public void setOvertimeAmount(BigDecimal overtimeAmount) {
        this.overtimeAmount = overtimeAmount;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
