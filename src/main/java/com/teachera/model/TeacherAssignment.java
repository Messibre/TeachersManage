package com.teachera.model;

import java.time.LocalDateTime;

public class TeacherAssignment {

    private Integer id;
    private Integer teacherId;
    private String subjectCode;
    private String className;
    private LocalDateTime createdAt;

    public TeacherAssignment() {}

    public TeacherAssignment(Integer id, Integer teacherId, String subjectCode, String className, LocalDateTime createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.subjectCode = subjectCode;
        this.className = className;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
