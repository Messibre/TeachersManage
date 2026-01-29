package com.teachera.service;

import com.teachera.model.Teacher;

import java.util.List;
import java.util.Optional;

/**
 * Business operations related to teacher management.
 *
 * Contains validation and business rules; delegates persistence to DAOs.
 */
public interface TeacherService {

    /**
     * Creates a new teacher after validating the data (e.g., unique code, required fields).
     */
    Teacher createTeacher(Teacher teacher);

    /**
     * Updates an existing teacher after validating new data.
     */
    Teacher updateTeacher(Teacher teacher);

    /**
     * Deletes a teacher. Implementation may enforce business rules
     * (e.g., disallow delete if payroll or attendance exists).
     */
    void deleteTeacher(int teacherId);

    Optional<Teacher> getTeacherById(int teacherId);

    Optional<Teacher> getTeacherByCode(String teacherCode);

    List<Teacher> getAllTeachers();

    // Assignment operations
    void assignClassToTeacher(int teacherId, String subjectCode, String className);
    java.util.List<com.teachera.model.TeacherAssignment> getAssignmentsForTeacher(int teacherId);
}

