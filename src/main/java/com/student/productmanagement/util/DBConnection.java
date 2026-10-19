package com.student.productmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/product_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("db.url", "DB_URL", DEFAULT_URL);
        String username = setting("db.username", "DB_USERNAME", null);
        String password = setting("db.password", "DB_PASSWORD", "");

        if (username == null || username.isBlank()) {
            throw new SQLException("Database username is not configured.");
        }
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentName);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
