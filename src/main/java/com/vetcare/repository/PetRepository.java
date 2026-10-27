package com.vetcare.repository;

import java.util.List;
import java.util.Optional;

import com.vetcare.domain.Pet;

public interface PetRepository {
    
    Pet save(Pet pet);
    Optional<Pet> findById(int  id);
    List<Pet> findByName(String name);
    List<Pet> findByClientId(int clientId);
    void update(Pet pet);
    void deleteById(int id);

}