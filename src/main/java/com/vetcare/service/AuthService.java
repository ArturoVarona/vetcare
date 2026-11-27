package com.vetcare.service;

import com.vetcare.domain.User;
import com.vetcare.exception.AuthenticationException;
import com.vetcare.exception.BusinessRule;
import com.vetcare.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static final String INVALID = "Usuario o contraseña incorrectos";

    private final AuthClient authClient;
    private final UserRepository userRepository;

    public AuthService(AuthClient authClient, UserRepository userRepository) {
        this.authClient = authClient;
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            throw new BusinessRule("Ingrese correo y contraseña");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessRule("La contraseña es demasiado larga");
        }
        UUID id;
        try {
            id = authClient.authenticate(email.trim(), password);
        } catch (AuthenticationException e) {
            LOGGER.warning("Intento de acceso fallido para: " + email);
            throw new AuthenticationException(INVALID);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException(INVALID));
        if (!user.isActive()) {
            LOGGER.warning("Usuario inactivo intentó entrar: " + email);
            throw new AuthenticationException(INVALID);
        }
        LOGGER.info("Inicio de sesión correcto: " + email);
        return user;
    }
}