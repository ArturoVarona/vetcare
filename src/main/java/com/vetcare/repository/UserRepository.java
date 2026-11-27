package com.vetcare.repository;

import com.vetcare.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
}