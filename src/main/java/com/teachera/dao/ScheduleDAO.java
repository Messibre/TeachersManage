package com.teachera.dao;

import com.teachera.model.Schedule;
import java.util.List;
import java.util.Optional;

public interface ScheduleDAO {
    void save(Schedule s);
    List<Schedule> findByTeacherId(int teacherId);
    List<Schedule> findByDayAndClass(String dayOfWeek, String className);
    List<Schedule> findAll();
    Optional<Schedule> findById(int id);
}
