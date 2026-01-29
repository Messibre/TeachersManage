package com.teachera.dao;

import com.teachera.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectDAO {

    void save(Subject subject);

    void update(Subject subject);

    void deleteById(int id);

    Optional<Subject> findById(int id);

    Optional<Subject> findByCode(String code);

    List<Subject> findAll();
}
