package com.vetcare.service;

import java.util.UUID;

public interface AuthClient {
    UUID authenticate(String email, String password);
}