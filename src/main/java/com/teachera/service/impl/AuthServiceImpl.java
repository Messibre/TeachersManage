package com.teachera.service.impl;

import com.teachera.dao.UserDAO;
import com.teachera.model.Role;
import com.teachera.model.User;
import com.teachera.service.AuthService;
import com.teachera.service.ServiceException;
import com.teachera.service.ValidationUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import com.teachera.model.User;
import com.teachera.model.Role;

public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO;

    private User currentUser;

    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO must not be null");
    }

    @Override
    public Optional<User> login(String username, String password) {
        String trimmedUsername = ValidationUtils.requireNonEmpty(username, "Username is required.");
        String trimmedPassword = ValidationUtils.requireNonEmpty(password, "Password is required.");

        String passwordHash = hashPassword(trimmedPassword);

        Optional<User> userOpt = userDAO.findByUsername(trimmedUsername)
                .filter(User::isActive)
                .filter(u -> passwordHash.equals(u.getPasswordHash()));

        currentUser = userOpt.orElse(null);
        return userOpt;
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    @Override
    public Optional<User> register(String username, String password) {
        String trimmedUsername = ValidationUtils.requireNonEmpty(username, "Username is required.");
        String trimmedPassword = ValidationUtils.requireNonEmpty(password, "Password is required.");

        userDAO.findByUsername(trimmedUsername).ifPresent(u -> {
            throw new ServiceException("Username already exists: " + trimmedUsername);
        });

        String passwordHash = hashPassword(trimmedPassword);

        User user = new User(null, trimmedUsername, passwordHash, Role.TEACHER, true, null);

        userDAO.save(user);

        currentUser = user;

        return Optional.of(user);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
