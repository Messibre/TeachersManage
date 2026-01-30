package com.teachera.dao.impl;

import com.teachera.dao.DaoException;
import com.teachera.dao.TeacherDAO;
import com.teachera.model.EmploymentType;
import com.teachera.model.Teacher;
import com.teachera.util.DbConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public void save(Teacher teacher) {
        final String sql = "INSERT INTO teachers (user_id, teacher_code, full_name, qualification, employment_type, " +
            "subject_specialty, contact_email, contact_phone, base_salary, hire_date, approved) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (teacher.getUserId() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, teacher.getUserId());
            }
            ps.setString(2, teacher.getTeacherCode());
            ps.setString(3, teacher.getFullName());
            ps.setString(4, teacher.getQualification());
            ps.setString(5, teacher.getEmploymentType().name());
            ps.setString(6, teacher.getSubjectSpecialty());
            ps.setString(7, teacher.getContactEmail());
            ps.setString(8, teacher.getContactPhone());
            ps.setBigDecimal(9, defaultMoney(teacher.getBaseSalary()));
            if (teacher.getHireDate() == null) {
                ps.setNull(10, java.sql.Types.DATE);
            } else {
                ps.setDate(10, Date.valueOf(teacher.getHireDate()));
            }
            ps.setBoolean(11, teacher.isApproved());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    teacher.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to save teacher", e);
        }
    }

    @Override
    public void update(Teacher teacher) {
        final String sql = "UPDATE teachers SET user_id=?, teacher_code=?, full_name=?, qualification=?, employment_type=?, " +
            "subject_specialty=?, contact_email=?, contact_phone=?, base_salary=?, hire_date=?, approved=? WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (teacher.getUserId() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, teacher.getUserId());
            }
            ps.setString(2, teacher.getTeacherCode());
            ps.setString(3, teacher.getFullName());
            ps.setString(4, teacher.getQualification());
            ps.setString(5, teacher.getEmploymentType().name());
            ps.setString(6, teacher.getSubjectSpecialty());
            ps.setString(7, teacher.getContactEmail());
            ps.setString(8, teacher.getContactPhone());
            ps.setBigDecimal(9, defaultMoney(teacher.getBaseSalary()));
            if (teacher.getHireDate() == null) {
                ps.setNull(10, java.sql.Types.DATE);
            } else {
                ps.setDate(10, Date.valueOf(teacher.getHireDate()));
            }
            ps.setBoolean(11, teacher.isApproved());
            ps.setInt(12, teacher.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update teacher id=" + teacher.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        final String sql = "DELETE FROM teachers WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to delete teacher id=" + id, e);
        }
    }

    @Override
    public Optional<Teacher> findById(int id) {
        final String sql = "SELECT id, user_id, teacher_code, full_name, qualification, employment_type, subject_specialty, " +
            "contact_email, contact_phone, base_salary, hire_date, approved FROM teachers WHERE id=?";
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
            throw new DaoException("Failed to find teacher id=" + id, e);
        }
    }

    @Override
    public Optional<Teacher> findByTeacherCode(String teacherCode) {
        final String sql = "SELECT id, user_id, teacher_code, full_name, qualification, employment_type, subject_specialty, " +
            "contact_email, contact_phone, base_salary, hire_date, approved FROM teachers WHERE teacher_code=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find teacher code=" + teacherCode, e);
        }
    }

    @Override
    public List<Teacher> findAll() {
        final String sql = "SELECT id, user_id, teacher_code, full_name, qualification, employment_type, subject_specialty, " +
            "contact_email, contact_phone, base_salary, hire_date, approved FROM teachers ORDER BY id";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                teachers.add(mapRow(rs));
            }
            return teachers;
        } catch (SQLException e) {
            throw new DaoException("Failed to list teachers", e);
        }
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer userId = rs.getObject("user_id") == null ? null : rs.getInt("user_id");
        String teacherCode = rs.getString("teacher_code");
        String fullName = rs.getString("full_name");
        String qualification = rs.getString("qualification");
        EmploymentType employmentType = EmploymentType.valueOf(rs.getString("employment_type"));
        String subjectSpecialty = rs.getString("subject_specialty");
        String contactEmail = rs.getString("contact_email");
        String contactPhone = rs.getString("contact_phone");
        BigDecimal baseSalary = rs.getBigDecimal("base_salary");
        Date hireDate = rs.getDate("hire_date");

        boolean approved = rs.getBoolean("approved");
        return new Teacher(id, userId, teacherCode, fullName, qualification, employmentType, subjectSpecialty,
            contactEmail, contactPhone, baseSalary, hireDate == null ? null : hireDate.toLocalDate(), approved);
    }

    private BigDecimal defaultMoney(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}

