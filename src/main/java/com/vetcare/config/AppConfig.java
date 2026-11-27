package com.vetcare.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class AppConfig {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private AppConfig() {
    }

    public static String get(String key) {
        String value = System.getenv(
                key.toUpperCase().replace('.', '_'));

        if (value == null || value.isBlank()) {
            value = DOTENV.get(key);
        }

        return value;
    }

    public static String require(String key) {
        String value = get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Falta la variable de configuracion: " + key);
        }

        return value;
    }
}
