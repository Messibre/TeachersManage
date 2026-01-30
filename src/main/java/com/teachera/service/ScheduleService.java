package com.teachera.service;

import com.teachera.model.Schedule;

import java.util.List;

public interface ScheduleService {
    void createSchedule(Schedule s);
    List<Schedule> getSchedulesForTeacher(int teacherId);
    List<Schedule> getSchedulesForDayAndClass(String dayOfWeek, String className);
    java.util.List<Schedule> getAllSchedules();
}
