package com.vetcare.repository;

import java.util.Optional;

import com.vetcare.domain.Owner;

public interface OwnerRepository {
    Optional<Owner> findById(long id);
}