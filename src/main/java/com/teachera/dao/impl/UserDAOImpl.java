package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.UserDAO;
import com.teachera.model.Role;
import com.teachera.model.User;
import com.teachera.util.DbConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public void save(User user) {
        final String sql = "INSERT INTO users (username, password_hash, role, active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, user.isActive());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save user", e);
        }
    }

    @Override
    public void update(User user) {
        final String sql = "UPDATE users SET username=?, password_hash=?, role=?, active=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update user id=" + user.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete user id=" + id, e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        final String sql = "SELECT id, username, password_hash, role, active, created_at FROM users WHERE id=?";
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
            throw new DaoException("Failed to find user id=" + id, e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final String sql = "SELECT id, username, password_hash, role, active, created_at FROM users WHERE username=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find user username=" + username, e);
        }
    }

    @Override
    public List<User> findAll() {
        final String sql = "SELECT id, username, password_hash, role, active, created_at FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DaoException("Failed to list users", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        Role role = Role.valueOf(rs.getString("role"));
        boolean active = rs.getBoolean("active");
        Timestamp createdTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTs == null ? null : createdTs.toLocalDateTime();

        return new User(id, username, passwordHash, role, active, createdAt);
    }
}

