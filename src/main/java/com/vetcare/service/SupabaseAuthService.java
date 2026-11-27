package com.vetcare.service;

import com.vetcare.config.AppConfig;
import com.vetcare.exception.AuthenticationException;
import com.vetcare.exception.DataAccessException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupabaseAuthService implements AuthClient {

    private static final Logger LOGGER = Logger.getLogger(SupabaseAuthService.class.getName());
    private static final Pattern USER_ID =
            Pattern.compile("\"id\"\\s*:\\s*\"([0-9a-fA-F\\-]{36})\"");

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public UUID authenticate(String email, String password) {
        String url = AppConfig.require("SUPABASE_URL") + "/auth/v1/token?grant_type=password";
        String body = "{\"email\":\"" + escape(email) + "\",\"password\":\"" + escape(password) + "\"}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("apikey", AppConfig.require("SUPABASE_ANON_KEY"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status == 400 || status == 401) {
                throw new AuthenticationException("Credenciales inválidas");
            }
            if (status != 200) {
                LOGGER.severe("Supabase Auth respondió con estado " + status);
                throw new DataAccessException("No se pudo contactar el servicio de autenticación", null);
            }
            return extractUserId(response.body());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.log(Level.SEVERE, "Error al contactar Supabase Auth", e);
            throw new DataAccessException("No se pudo contactar el servicio de autenticación", e);
        }
    }

    private UUID extractUserId(String json) {
        int userStart = json.indexOf("\"user\"");
        Matcher m = USER_ID.matcher(userStart >= 0 ? json.substring(userStart) : json);
        if (!m.find()) {
            throw new AuthenticationException("Respuesta de autenticación inválida");
        }
        return UUID.fromString(m.group(1));
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}