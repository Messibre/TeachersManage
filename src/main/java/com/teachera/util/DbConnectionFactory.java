package com.teachera.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public final class DbConnectionFactory {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DbConnectionFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + PROPERTIES_FILE + " on classpath.");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + PROPERTIES_FILE, e);
        }
    }

    private DbConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        String url = required("db.url");
        String username = required("db.username");
        String password = required("db.password");
        return DriverManager.getConnection(url, username, password);
    }

    private static String required(String key) {
        return Objects.requireNonNull(PROPS.getProperty(key), "Missing property: " + key).trim();
    }
}
