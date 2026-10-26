package com.vetcare;

import java.sql.Connection;
import java.sql.SQLException;

import com.vetcare.config.DatabaseConnection;


public class DatabaseTest {
     public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Conexion con Supabase exitosa.");
            System.out.println("Base de datos: "
                    + connection.getCatalog());
        } catch (SQLException e) {
            System.err.println("Error al conectar con Supabase.");
            System.err.println(e.getMessage());
        }
    }

}
