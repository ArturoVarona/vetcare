
package com.vetcare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.vetcare.service.SupabaseAuthService;
public class SupabaseAuthTest {

    @Test
    void debeIniciarSesionEnSupabase() throws Exception {
        String email = System.getenv("SUPABASE_TEST_EMAIL");
        String password = System.getenv("SUPABASE_TEST_PASSWORD");

        if (email == null || password == null) {
            throw new IllegalStateException(
                    "Configura SUPABASE_TEST_EMAIL y SUPABASE_TEST_PASSWORD."
            );
        }

        SupabaseAuthService auth = new SupabaseAuthService();
        int status = auth.iniciarSesion(email, password);

        assertEquals(200, status,
                "Supabase no aceptó el inicio de sesión.");
    }
}
