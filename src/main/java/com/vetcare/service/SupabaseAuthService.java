package com.vetcare.service;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SupabaseAuthService {
     private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public int iniciarSesion(String email, String password)
            throws IOException, InterruptedException {

        String url = System.getenv("SUPABASE_URL");
        String apiKey = System.getenv("SUPABASE_PUBLISHABLE_KEY");

        if (url == null || apiKey == null) {
            throw new IllegalStateException(
                    "Falta configurar las variables de Supabase."
            );
        }

        String json = "{\"email\":\"" + escaparJson(email)
                + "\",\"password\":\"" + escaparJson(password) + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/auth/v1/token?grant_type=password"))
                .timeout(Duration.ofSeconds(20))
                .header("apikey", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

       System.out.println("Código HTTP: " + response.statusCode());
       System.out.println("Respuesta de Supabase: " + response.body());
       
       
       return response.statusCode();
    }

    private String escaparJson(String texto) {
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
