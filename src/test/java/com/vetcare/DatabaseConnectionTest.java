
package com.vetcare;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.vetcare.config.DatabaseConnection;

@Tag("integration")
class DatabaseConnectionTest {

    @Test
    void debeConectarseASupabase() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection, "La conexión no debe ser nula");
            assertFalse(connection.isClosed(), "La conexión debe estar abierta");
            assertTrue(connection.isValid(5), "La conexión debe ser válida");

            System.out.println("Conexion con Supabase exitosa.");
        }
    }
}
