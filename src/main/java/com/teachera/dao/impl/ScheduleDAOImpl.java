package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.ScheduleDAO;
import com.teachera.model.Schedule;
import com.teachera.util.DbConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScheduleDAOImpl implements ScheduleDAO {

    @Override
    public void save(Schedule s) {
        final String sql = "INSERT INTO schedules (teacher_id, day_of_week, class_name, subject_code, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, s.getTeacherId());
            ps.setString(2, s.getDayOfWeek());
            ps.setString(3, s.getClassName());
            ps.setString(4, s.getSubjectCode());
            ps.setTime(5, java.sql.Time.valueOf(s.getStartTime()));
            ps.setTime(6, java.sql.Time.valueOf(s.getEndTime()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    s.setId(keys.getInt(1));
            }

            if (s.getSections() != null && !s.getSections().isEmpty()) {
                final String ins = "INSERT INTO schedule_sections (schedule_id, section) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                    for (Integer sec : s.getSections()) {
                        ps2.setInt(1, s.getId());
                        ps2.setInt(2, sec);
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save schedule", e);
        }
    }

    @Override
    public List<Schedule> findByTeacherId(int teacherId) {
        final String sql = "SELECT id, teacher_id, day_of_week, class_name, subject_code, start_time, end_time, created_at FROM schedules WHERE teacher_id=? ORDER BY day_of_week, start_time";
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list schedules for teacher", e);
        }
    }

    @Override
    public List<Schedule> findByDayAndClass(String dayOfWeek, String className) {
        final String sql = "SELECT DISTINCT schedules.id, teacher_id, day_of_week, class_name, subject_code, start_time, end_time, created_at "
                +
                "FROM schedules LEFT JOIN schedule_sections ss ON ss.schedule_id = schedules.id " +
                "WHERE schedules.day_of_week=? AND (schedules.class_name=? OR ss.section=?)";
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayOfWeek);
            ps.setString(2, className);
            try {
                ps.setInt(3, Integer.parseInt(className));
            } catch (NumberFormatException nfe) {
                ps.setInt(3, -1);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list schedules for class/day", e);
        }
    }

    @Override
    public List<Schedule> findAll() {
        final String sql = "SELECT id, teacher_id, day_of_week, class_name, subject_code, start_time, end_time, created_at FROM schedules ORDER BY day_of_week, start_time";
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list schedules", e);
        }
    }

    @Override
    public Optional<Schedule> findById(int id) {
        final String sql = "SELECT id, teacher_id, day_of_week, class_name, subject_code, start_time, end_time, created_at FROM schedules WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find schedule id=" + id, e);
        }
    }

    private Schedule mapRow(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getInt("id"));
        s.setTeacherId(rs.getInt("teacher_id"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setClassName(rs.getString("class_name"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setStartTime(rs.getTime("start_time").toLocalTime());
        s.setEndTime(rs.getTime("end_time").toLocalTime());
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        s.setCreatedAt(ts == null ? null : ts.toLocalDateTime());

        List<Integer> secs = new ArrayList<>();
        final String q = "SELECT section FROM schedule_sections WHERE schedule_id = ?";
        try (Connection conn2 = DbConnectionFactory.getConnection();
                PreparedStatement ps2 = conn2.prepareStatement(q)) {
            ps2.setInt(1, s.getId());
            try (ResultSet r2 = ps2.executeQuery()) {
                while (r2.next())
                    secs.add(r2.getInt("section"));
            }
        } catch (java.sql.SQLException ex) {

            ex.printStackTrace();
        }
        s.setSections(secs);
        return s;
    }
}
