package com.teachera.dao.impl;

import com.teachera.dao.AttendanceDAO;
import com.teachera.dao.DaoException;
import com.teachera.model.Attendance;
import com.teachera.model.AttendanceStatus;
import com.teachera.util.DbConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public void save(Attendance attendance) {
        final String sql = "INSERT INTO attendance (teacher_id, attendance_date, status, hours_worked, remarks) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, attendance.getTeacherId());
            ps.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            ps.setString(3, attendance.getStatus().name());
            if (attendance.getHoursWorked() == null) {
                ps.setNull(4, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, attendance.getHoursWorked());
            }
            ps.setString(5, attendance.getRemarks());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    attendance.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save attendance", e);
        }
    }

    @Override
    public void update(Attendance attendance) {
        final String sql = "UPDATE attendance SET teacher_id=?, attendance_date=?, status=?, hours_worked=?, remarks=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attendance.getTeacherId());
            ps.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            ps.setString(3, attendance.getStatus().name());
            if (attendance.getHoursWorked() == null) {
                ps.setNull(4, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, attendance.getHoursWorked());
            }
            ps.setString(5, attendance.getRemarks());
            ps.setInt(6, attendance.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update attendance id=" + attendance.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM attendance WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete attendance id=" + id, e);
        }
    }

    @Override
    public Optional<Attendance> findById(int id) {
        final String sql = "SELECT id, teacher_id, attendance_date, status, hours_worked, remarks, created_at " +
                "FROM attendance WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find attendance id=" + id, e);
        }
    }

    @Override
    public Optional<Attendance> findByTeacherAndDate(int teacherId, LocalDate date) {
        final String sql = "SELECT id, teacher_id, attendance_date, status, hours_worked, remarks, created_at " +
                "FROM attendance WHERE teacher_id=? AND attendance_date=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find attendance for teacherId=" + teacherId + " date=" + date, e);
        }
    }

    @Override
    public List<Attendance> findByTeacher(int teacherId) {
        final String sql = "SELECT id, teacher_id, attendance_date, status, hours_worked, remarks, created_at " +
                "FROM attendance WHERE teacher_id=? ORDER BY attendance_date DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list attendance for teacherId=" + teacherId, e);
        }
    }

    @Override
    public List<Attendance> findAll() {
        final String sql = "SELECT id, teacher_id, attendance_date, status, hours_worked, remarks, created_at " +
                "FROM attendance ORDER BY attendance_date DESC, id DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list attendance records", e);
        }
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer teacherId = rs.getInt("teacher_id");
        LocalDate attendanceDate = rs.getDate("attendance_date").toLocalDate();
        AttendanceStatus status = AttendanceStatus.valueOf(rs.getString("status"));
        BigDecimal hoursWorked = rs.getBigDecimal("hours_worked");
        String remarks = rs.getString("remarks");
        Timestamp createdTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTs == null ? null : createdTs.toLocalDateTime();

        return new Attendance(id, teacherId, attendanceDate, status, hoursWorked, remarks, createdAt);
    }
}

