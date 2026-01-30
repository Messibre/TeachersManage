package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.TeacherAssignmentDAO;
import com.teachera.model.TeacherAssignment;
import com.teachera.util.DbConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherAssignmentDAOImpl implements TeacherAssignmentDAO {

    @Override
    public void save(TeacherAssignment a) {
        final String sql = "INSERT INTO teacher_assignments (teacher_id, subject_code, class_name) VALUES (?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getTeacherId());
            ps.setString(2, a.getSubjectCode());
            ps.setString(3, a.getClassName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save assignment", e);
        }
    }

    @Override
    public List<TeacherAssignment> findByTeacherId(int teacherId) {
        final String sql = "SELECT id, teacher_id, subject_code, class_name, created_at FROM teacher_assignments WHERE teacher_id=? ORDER BY id";
        List<TeacherAssignment> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    String subject = rs.getString("subject_code");
                    String cls = rs.getString("class_name");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime created = ts == null ? null : ts.toLocalDateTime();
                    list.add(new TeacherAssignment(id, teacherId, subject, cls, created));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list assignments", e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM teacher_assignments WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete assignment id=" + id, e);
        }
    }

    @Override
    public Optional<TeacherAssignment> findById(int id) {
        final String sql = "SELECT id, teacher_id, subject_code, class_name, created_at FROM teacher_assignments WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer teacherId = rs.getInt("teacher_id");
                    String subject = rs.getString("subject_code");
                    String cls = rs.getString("class_name");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime created = ts == null ? null : ts.toLocalDateTime();
                    return Optional.of(new TeacherAssignment(id, teacherId, subject, cls, created));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find assignment id=" + id, e);
        }
    }
}
