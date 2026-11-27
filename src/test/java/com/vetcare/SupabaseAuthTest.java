package com.vetcare;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vetcare.config.AppConfig;
import com.vetcare.exception.AuthenticationException;
import com.vetcare.service.SupabaseAuthService;
import java.util.UUID;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class SupabaseAuthTest {

    @Test
    void debeIniciarSesionEnSupabase() {
        String email = AppConfig.get("SUPABASE_TEST_EMAIL");
        String password = AppConfig.get("SUPABASE_TEST_PASSWORD");
        Assumptions.assumeTrue(email != null && password != null,
                "Falta SUPABASE_TEST_EMAIL o SUPABASE_TEST_PASSWORD en el .env");

        UUID id = new SupabaseAuthService().authenticate(email, password);
        assertNotNull(id);
    }

    @Test
    void debeRechazarCredencialesIncorrectas() {
        Assumptions.assumeTrue(AppConfig.get("SUPABASE_URL") != null, "Falta SUPABASE_URL");
        assertThrows(AuthenticationException.class,
                () -> new SupabaseAuthService().authenticate("nadie@noexiste.com", "clave-incorrecta"));
    }
}