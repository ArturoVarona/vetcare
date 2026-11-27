package com.vetcare.repository;

import java.util.List;
import java.util.Optional;

import com.vetcare.domain.Pet;

public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(long id);
    List<Pet> findByOwnerId(long ownerId);
    List<Pet> findByName(String name);
    void update(Pet pet);
    void deleteById(long id);
}