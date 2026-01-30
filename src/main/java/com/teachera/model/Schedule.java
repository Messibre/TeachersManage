package com.teachera.model;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

public class Schedule {

    private Integer id;
    private Integer teacherId;
    private String dayOfWeek;
    private String className;
    private String subjectCode;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private List<Integer> sections;

    public Schedule() {
    }

    public Schedule(Integer id, Integer teacherId, String dayOfWeek, String className, String subjectCode,
            LocalTime startTime, LocalTime endTime, LocalDateTime createdAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.className = className;
        this.subjectCode = subjectCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
    }

    public List<Integer> getSections() {
        return sections;
    }

    public void setSections(List<Integer> sections) {
        this.sections = sections;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
