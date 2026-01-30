package com.teachera.dao;

import com.teachera.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    void save(User user);

    void update(User user);

    void deleteById(int id);

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
