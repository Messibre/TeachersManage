package com.teachera.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher {

    private Integer id;
    private Integer userId;
    private String teacherCode;
    private String fullName;
    private String qualification;
    private EmploymentType employmentType;
    private String subjectSpecialty;
    private String contactEmail;
    private String contactPhone;
    private BigDecimal baseSalary;
    private LocalDate hireDate;
    private boolean approved = false;

    public Teacher() {
    }

    public Teacher(Integer id,
            Integer userId,
            String teacherCode,
            String fullName,
            String qualification,
            EmploymentType employmentType,
            String subjectSpecialty,
            String contactEmail,
            String contactPhone,
            BigDecimal baseSalary,
            LocalDate hireDate) {
        this(id, userId, teacherCode, fullName, qualification, employmentType, subjectSpecialty, contactEmail,
                contactPhone, baseSalary, hireDate, false);
    }

    public Teacher(Integer id,
            Integer userId,
            String teacherCode,
            String fullName,
            String qualification,
            EmploymentType employmentType,
            String subjectSpecialty,
            String contactEmail,
            String contactPhone,
            BigDecimal baseSalary,
            LocalDate hireDate,
            boolean approved) {
        this.id = id;
        this.userId = userId;
        this.teacherCode = teacherCode;
        this.fullName = fullName;
        this.qualification = qualification;
        this.employmentType = employmentType;
        this.subjectSpecialty = subjectSpecialty;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.baseSalary = baseSalary;
        this.hireDate = hireDate;
        this.approved = approved;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public String getSubjectSpecialty() {
        return subjectSpecialty;
    }

    public void setSubjectSpecialty(String subjectSpecialty) {
        this.subjectSpecialty = subjectSpecialty;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
