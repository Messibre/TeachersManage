package com.teachera.dao;

import com.teachera.model.Leave;
import com.teachera.model.LeaveStatus;

import java.util.List;
import java.util.Optional;

public interface LeaveDAO {

    void save(Leave leave);

    void update(Leave leave);

    void deleteById(int id);

    Optional<Leave> findById(int id);

    List<Leave> findByTeacher(int teacherId);

    List<Leave> findByStatus(LeaveStatus status);

    List<Leave> findAll();
}
