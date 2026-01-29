package com.teachera.service;

import com.teachera.model.Role;
import com.teachera.model.User;

import java.util.Optional;

public interface AuthService {

    Optional<User> login(String username, String password);

    Optional<User> getCurrentUser();

    void logout();

    boolean hasRole(Role role);

    java.util.Optional<com.teachera.model.User> register(String username, String password);

}
