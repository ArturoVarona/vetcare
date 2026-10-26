package com.vetcare;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.vetcare.config.AppConfig;
import com.vetcare.config.DatabaseConnection;

@Tag("integration")
public class DatabaseConnectionTest {

    @Test
    void debeConectarseASupabase() throws Exception {
        // Si no hay credenciales, el test se omite en vez de fallar
        assumeTrue(AppConfig.get("postgres.nxktftxnoklbhzcgzbea") != null,
                "No hay DB_URL configurada, se omite el test");

        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            assertTrue(connection.isValid(5));
            System.out.println("Conexion con Supabase exitosa.");
        }
    }
}
