package com.teachera.service.impl;

import com.teachera.dao.TeacherDAO;
import com.teachera.model.Teacher;
import com.teachera.service.ServiceException;
import com.teachera.service.TeacherService;
import com.teachera.service.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.teachera.model.EmploymentType;

public class TeacherServiceImpl implements TeacherService {

    private final TeacherDAO teacherDAO;
    private final com.teachera.dao.TeacherAssignmentDAO assignmentDAO;

    public TeacherServiceImpl(TeacherDAO teacherDAO) {
        this(teacherDAO, null);
    }

    public TeacherServiceImpl(TeacherDAO teacherDAO, com.teachera.dao.TeacherAssignmentDAO assignmentDAO) {
        this.teacherDAO = Objects.requireNonNull(teacherDAO, "teacherDAO must not be null");
        this.assignmentDAO = assignmentDAO;
    }

    @Override
    public Teacher createTeacher(Teacher teacher) {

        validateForCreateOrUpdate(teacher, false);

        if (teacher.getTeacherCode() != null && !teacher.getTeacherCode().trim().isEmpty()) {
            teacherDAO.findByTeacherCode(teacher.getTeacherCode())
                    .ifPresent(t -> {
                        throw new ServiceException("Teacher code already exists: " + teacher.getTeacherCode());
                    });
        }

        if (teacher.getUserId() != null && teacher.getEmploymentType() == null) {
            teacher.setEmploymentType(EmploymentType.FULL_TIME);
        }

        if (teacher.getTeacherCode() == null || teacher.getTeacherCode().isBlank()) {
            String gen = "T" + (System.currentTimeMillis() % 100000);
            teacher.setTeacherCode(gen);
        }

        teacherDAO.save(teacher);
        return teacher;
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new ServiceException("Teacher ID is required for update.");
        }

        validateForCreateOrUpdate(teacher, true);

        teacherDAO.findByTeacherCode(teacher.getTeacherCode())
                .filter(existing -> !existing.getId().equals(teacher.getId()))
                .ifPresent(existing -> {
                    throw new ServiceException("Another teacher already uses code: " + teacher.getTeacherCode());
                });

        teacherDAO.update(teacher);
        return teacher;
    }

    @Override
    public void deleteTeacher(int teacherId) {

        teacherDAO.deleteById(teacherId);
    }

    @Override
    public Optional<Teacher> getTeacherById(int teacherId) {
        return teacherDAO.findById(teacherId);
    }

    @Override
    public Optional<Teacher> getTeacherByCode(String teacherCode) {
        return teacherDAO.findByTeacherCode(teacherCode);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    @Override
    public void assignClassToTeacher(int teacherId, String subjectCode, String className) {
        if (assignmentDAO == null)
            throw new ServiceException("Assignment feature not configured.");
        com.teachera.model.TeacherAssignment a = new com.teachera.model.TeacherAssignment();
        a.setTeacherId(teacherId);
        a.setSubjectCode(subjectCode);
        a.setClassName(className);
        assignmentDAO.save(a);
    }

    @Override
    public java.util.List<com.teachera.model.TeacherAssignment> getAssignmentsForTeacher(int teacherId) {
        if (assignmentDAO == null)
            return java.util.List.of();
        return assignmentDAO.findByTeacherId(teacherId);
    }

    private void validateForCreateOrUpdate(Teacher teacher, boolean isUpdate) {
        if (teacher == null) {
            throw new ServiceException("Teacher is required.");
        }

        if (teacher.getUserId() != null && !isUpdate) {
            ValidationUtils.requireNonEmpty(teacher.getFullName(), "Teacher full name is required.");
            return;
        }

        if (isUpdate && teacher.isApproved()) {
            ValidationUtils.requireNonEmpty(teacher.getFullName(), "Teacher full name is required.");
            if (teacher.getTeacherCode() == null || teacher.getTeacherCode().isBlank()) {

                String gen = "T" + (teacher.getId() == null ? System.currentTimeMillis() % 100000 : teacher.getId());
                teacher.setTeacherCode(gen);
            }
            if (teacher.getEmploymentType() == null) {
                teacher.setEmploymentType(EmploymentType.FULL_TIME);
            }
            return;
        }

        ValidationUtils.requireNonEmpty(teacher.getTeacherCode(), "Teacher code is required.");
        ValidationUtils.requireNonEmpty(teacher.getFullName(), "Teacher full name is required.");
        ValidationUtils.requireNotNull(teacher.getEmploymentType(), "Employment type is required.");

        BigDecimal baseSalary = teacher.getBaseSalary();
        if (baseSalary == null) {
            teacher.setBaseSalary(BigDecimal.ZERO);
        } else if (baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("Base salary must be zero or positive.");
        }
    }
}
