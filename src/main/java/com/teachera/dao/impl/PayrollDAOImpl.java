package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.PayrollDAO;
import com.teachera.model.Payroll;
import com.teachera.util.DbConnectionFactory;

import java.math.BigDecimal;
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

public class PayrollDAOImpl implements PayrollDAO {

    @Override
    public void save(Payroll payroll) {
        final String sql = "INSERT INTO payroll (teacher_id, period_year, period_month, base_salary, overtime_hours, overtime_amount, deductions, net_salary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payroll.getTeacherId());
            ps.setInt(2, payroll.getPeriodYear());
            ps.setInt(3, payroll.getPeriodMonth());
            ps.setBigDecimal(4, defaultMoney(payroll.getBaseSalary()));
            ps.setBigDecimal(5, defaultMoney(payroll.getOvertimeHours()));
            ps.setBigDecimal(6, defaultMoney(payroll.getOvertimeAmount()));
            ps.setBigDecimal(7, defaultMoney(payroll.getDeductions()));
            ps.setBigDecimal(8, defaultMoney(payroll.getNetSalary()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    payroll.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save payroll", e);
        }
    }

    @Override
    public void update(Payroll payroll) {
        final String sql = "UPDATE payroll SET teacher_id=?, period_year=?, period_month=?, base_salary=?, overtime_hours=?, " +
                "overtime_amount=?, deductions=?, net_salary=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payroll.getTeacherId());
            ps.setInt(2, payroll.getPeriodYear());
            ps.setInt(3, payroll.getPeriodMonth());
            ps.setBigDecimal(4, defaultMoney(payroll.getBaseSalary()));
            ps.setBigDecimal(5, defaultMoney(payroll.getOvertimeHours()));
            ps.setBigDecimal(6, defaultMoney(payroll.getOvertimeAmount()));
            ps.setBigDecimal(7, defaultMoney(payroll.getDeductions()));
            ps.setBigDecimal(8, defaultMoney(payroll.getNetSalary()));
            ps.setInt(9, payroll.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update payroll id=" + payroll.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM payroll WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete payroll id=" + id, e);
        }
    }

    @Override
    public Optional<Payroll> findById(int id) {
        final String sql = "SELECT id, teacher_id, period_year, period_month, base_salary, overtime_hours, overtime_amount, " +
                "deductions, net_salary, generated_at FROM payroll WHERE id=?";
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
            throw new DaoException("Failed to find payroll id=" + id, e);
        }
    }

    @Override
    public Optional<Payroll> findByTeacherAndPeriod(int teacherId, int year, int month) {
        final String sql = "SELECT id, teacher_id, period_year, period_month, base_salary, overtime_hours, overtime_amount, " +
                "deductions, net_salary, generated_at FROM payroll WHERE teacher_id=? AND period_year=? AND period_month=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find payroll for teacherId=" + teacherId + " period=" + year + "-" + month, e);
        }
    }

    @Override
    public List<Payroll> findByTeacher(int teacherId) {
        final String sql = "SELECT id, teacher_id, period_year, period_month, base_salary, overtime_hours, overtime_amount, " +
                "deductions, net_salary, generated_at FROM payroll WHERE teacher_id=? ORDER BY period_year DESC, period_month DESC";
        List<Payroll> list = new ArrayList<>();
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
            throw new DaoException("Failed to list payroll for teacherId=" + teacherId, e);
        }
    }

    @Override
    public List<Payroll> findAll() {
        final String sql = "SELECT id, teacher_id, period_year, period_month, base_salary, overtime_hours, overtime_amount, " +
                "deductions, net_salary, generated_at FROM payroll ORDER BY period_year DESC, period_month DESC, id DESC";
        List<Payroll> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Failed to list payroll records", e);
        }
    }

    private Payroll mapRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer teacherId = rs.getInt("teacher_id");
        int year = rs.getInt("period_year");
        int month = rs.getInt("period_month");
        BigDecimal baseSalary = rs.getBigDecimal("base_salary");
        BigDecimal overtimeHours = rs.getBigDecimal("overtime_hours");
        BigDecimal overtimeAmount = rs.getBigDecimal("overtime_amount");
        BigDecimal deductions = rs.getBigDecimal("deductions");
        BigDecimal netSalary = rs.getBigDecimal("net_salary");
        Timestamp generatedTs = rs.getTimestamp("generated_at");
        LocalDateTime generatedAt = generatedTs == null ? null : generatedTs.toLocalDateTime();

        return new Payroll(id, teacherId, year, month, baseSalary, overtimeHours, overtimeAmount, deductions, netSalary, generatedAt);
    }

    private BigDecimal defaultMoney(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

