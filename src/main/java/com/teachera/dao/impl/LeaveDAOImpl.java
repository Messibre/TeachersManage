package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.LeaveDAO;
import com.teachera.model.Leave;
import com.teachera.model.LeaveStatus;
import com.teachera.model.LeaveType;
import com.teachera.util.DbConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveDAOImpl implements LeaveDAO {

    @Override
    public void save(Leave leave) {
        final String sql = "INSERT INTO leaves (teacher_id, start_date, end_date, leave_type, status, reason, reviewed_by, reviewed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, leave.getTeacherId());
            ps.setDate(2, Date.valueOf(leave.getStartDate()));
            ps.setDate(3, Date.valueOf(leave.getEndDate()));
            ps.setString(4, leave.getLeaveType().name());
            ps.setString(5, leave.getStatus().name());
            ps.setString(6, leave.getReason());

            if (leave.getReviewedByUserId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, leave.getReviewedByUserId());
            }

            if (leave.getReviewedAt() == null) {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(leave.getReviewedAt()));
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    leave.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save leave", e);
        }
    }

    @Override
    public void update(Leave leave) {
        final String sql = "UPDATE leaves SET teacher_id=?, start_date=?, end_date=?, leave_type=?, status=?, reason=?, " +
                "reviewed_by=?, reviewed_at=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leave.getTeacherId());
            ps.setDate(2, Date.valueOf(leave.getStartDate()));
            ps.setDate(3, Date.valueOf(leave.getEndDate()));
            ps.setString(4, leave.getLeaveType().name());
            ps.setString(5, leave.getStatus().name());
            ps.setString(6, leave.getReason());

            if (leave.getReviewedByUserId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, leave.getReviewedByUserId());
            }

            if (leave.getReviewedAt() == null) {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(leave.getReviewedAt()));
            }

            ps.setInt(9, leave.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update leave id=" + leave.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM leaves WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete leave id=" + id, e);
        }
    }

    @Override
    public Optional<Leave> findById(int id) {
        final String sql = "SELECT id, teacher_id, start_date, end_date, leave_type, status, reason, requested_at, reviewed_by, reviewed_at " +
                "FROM leaves WHERE id=?";
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
            throw new DaoException("Failed to find leave id=" + id, e);
        }
    }

    @Override
    public List<Leave> findByTeacher(int teacherId) {
        final String sql = "SELECT id, teacher_id, start_date, end_date, leave_type, status, reason, requested_at, reviewed_by, reviewed_at " +
                "FROM leaves WHERE teacher_id=? ORDER BY requested_at DESC, id DESC";
        List<Leave> list = new ArrayList<>();
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
            throw new DaoException("Failed to list leaves for teacherId=" + teacherId, e);
        }
    }

    @Override
    public List<Leave> findByStatus(LeaveStatus status) {
        final String sql = "SELECT id, teacher_id, start_date, end_date, leave_type, status, reason, requested_at, reviewed_by, reviewed_at " +
                "FROM leaves WHERE status=? ORDER BY requested_at DESC, id DESC";
        List<Leave> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list leaves status=" + status, e);
        }
    }

    @Override
    public List<Leave> findAll() {
        final String sql = "SELECT id, teacher_id, start_date, end_date, leave_type, status, reason, requested_at, reviewed_by, reviewed_at " +
                "FROM leaves ORDER BY requested_at DESC, id DESC";
        List<Leave> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list leaves", e);
        }
    }

    private Leave mapRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer teacherId = rs.getInt("teacher_id");

        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");

        LeaveType leaveType = LeaveType.valueOf(rs.getString("leave_type"));
        LeaveStatus status = LeaveStatus.valueOf(rs.getString("status"));
        String reason = rs.getString("reason");

        Timestamp requestedTs = rs.getTimestamp("requested_at");
        LocalDateTime requestedAt = requestedTs == null ? null : requestedTs.toLocalDateTime();

        Integer reviewedBy = rs.getObject("reviewed_by") == null ? null : rs.getInt("reviewed_by");

        Timestamp reviewedTs = rs.getTimestamp("reviewed_at");
        LocalDateTime reviewedAt = reviewedTs == null ? null : reviewedTs.toLocalDateTime();

        return new Leave(id, teacherId,
                start == null ? null : start.toLocalDate(),
                end == null ? null : end.toLocalDate(),
                leaveType, status, reason, requestedAt, reviewedBy, reviewedAt);
    }
}

