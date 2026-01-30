package com.teachera.service.impl;

import com.teachera.dao.ScheduleDAO;
import com.teachera.dao.TeacherDAO;
import com.teachera.model.Schedule;
import com.teachera.service.ScheduleService;
import com.teachera.service.ServiceException;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleDAO scheduleDAO;
    private final TeacherDAO teacherDAO;

    public ScheduleServiceImpl(ScheduleDAO scheduleDAO, TeacherDAO teacherDAO) {
        this.scheduleDAO = Objects.requireNonNull(scheduleDAO);
        this.teacherDAO = Objects.requireNonNull(teacherDAO);
    }

    @Override
    public void createSchedule(Schedule s) {
        if (s == null)
            throw new ServiceException("Schedule is required");
        if (s.getTeacherId() == null)
            throw new ServiceException("Teacher is required for schedule");
        if (!teacherDAO.findById(s.getTeacherId()).isPresent())
            throw new ServiceException("Teacher not found");
        if (s.getStartTime() == null || s.getEndTime() == null)
            throw new ServiceException("Start and end times are required");
        if (!s.getStartTime().isBefore(s.getEndTime()))
            throw new ServiceException("Start time must be before end time");

        if (s.getSections() != null) {
            for (Integer sec : s.getSections()) {
                String className = String.valueOf(sec);
                List<Schedule> existing = scheduleDAO.findByDayAndClass(s.getDayOfWeek(), className);
                for (Schedule ex : existing) {
                    LocalTime a1 = ex.getStartTime();
                    LocalTime a2 = ex.getEndTime();
                    LocalTime b1 = s.getStartTime();
                    LocalTime b2 = s.getEndTime();
                    boolean overlap = b1.isBefore(a2) && a1.isBefore(b2);
                    if (overlap) {
                        throw new ServiceException(
                                "Schedule conflict detected for section " + className + " at " + s.getDayOfWeek());
                    }
                }
            }
        }

        scheduleDAO.save(s);
    }

    @Override
    public List<Schedule> getSchedulesForTeacher(int teacherId) {
        return scheduleDAO.findByTeacherId(teacherId);
    }

    @Override
    public List<Schedule> getSchedulesForDayAndClass(String dayOfWeek, String className) {
        return scheduleDAO.findByDayAndClass(dayOfWeek, className);
    }

    @Override
    public java.util.List<Schedule> getAllSchedules() {
        return scheduleDAO.findAll();
    }
}
