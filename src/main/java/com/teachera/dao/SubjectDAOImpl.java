package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.SubjectDAO;
import com.teachera.model.Subject;
import com.teachera.util.DbConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubjectDAOImpl implements SubjectDAO {

    @Override
    public void save(Subject subject) {
        final String sql = "INSERT INTO subjects (code, name, description) VALUES (?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, subject.getCode());
            ps.setString(2, subject.getName());
            ps.setString(3, subject.getDescription());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    subject.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save subject", e);
        }
    }

    @Override
    public void update(Subject subject) {
        final String sql = "UPDATE subjects SET code=?, name=?, description=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getCode());
            ps.setString(2, subject.getName());
            ps.setString(3, subject.getDescription());
            ps.setInt(4, subject.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update subject id=" + subject.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM subjects WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete subject id=" + id, e);
        }
    }

    @Override
    public Optional<Subject> findById(int id) {
        final String sql = "SELECT id, code, name, description FROM subjects WHERE id=?";
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
            throw new DaoException("Failed to find subject id=" + id, e);
        }
    }

    @Override
    public Optional<Subject> findByCode(String code) {
        final String sql = "SELECT id, code, name, description FROM subjects WHERE code=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find subject code=" + code, e);
        }
    }

    @Override
    public List<Subject> findAll() {
        final String sql = "SELECT id, code, name, description FROM subjects ORDER BY id";
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(mapRow(rs));
            }
            return subjects;
        } catch (SQLException e) {
            throw new DaoException("Failed to list subjects", e);
        }
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        return new Subject(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}

