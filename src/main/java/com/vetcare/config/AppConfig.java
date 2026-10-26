package com.vetcare.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class AppConfig {

    // Busca primero en variables de entorno del sistema y luego en el .env
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private AppConfig() {
    }

    public static String get(String key) {
        return DOTENV.get(key);
    }

    public static String require(String key) {
        String value = DOTENV.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Falta la variable de configuracion: " + key
                    + " (revisa tu archivo .env)");
        }
        return value;
    }
}