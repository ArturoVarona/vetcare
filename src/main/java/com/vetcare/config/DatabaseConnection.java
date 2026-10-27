package com.vetcare.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() throws SQLException {   // constructor privado: nadie hace "new"
            String url = AppConfig.require("DB_URL");
            String user = AppConfig.require("DB_USER");
            String password = AppConfig.require("DB_PASSWORD");
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {                          // se crea solo la primera vez
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }
}