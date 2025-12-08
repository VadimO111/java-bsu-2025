package com.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:./bank;AUTO_SERVER=TRUE", "sa", "");
            initTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (uuid VARCHAR(36) PRIMARY KEY, nickname VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (id VARCHAR(36) PRIMARY KEY, user_uuid VARCHAR(36), balance DOUBLE, is_frozen BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (uuid VARCHAR(36) PRIMARY KEY, type VARCHAR(50), amount DOUBLE, account_id VARCHAR(36), timestamp LONG)");
        }
    }
}