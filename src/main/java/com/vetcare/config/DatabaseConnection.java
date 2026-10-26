package com.vetcare.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = AppConfig.require("postgresql://postgres.nxktftxnoklbhzcgzbea:[YOUR-PASSWORD]@aws-0-us-east-1.pooler.supabase.com:5432/postgres");
        String user = AppConfig.require("postgres.nxktftxnoklbhzcgzbea");
        String password = AppConfig.require("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }
}
