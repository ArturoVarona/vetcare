package com.vetcare.service;

import com.vetcare.domain.Pet;
import com.vetcare.domain.Species;
import com.vetcare.exception.BusinessRule;
import com.vetcare.exception.EntityNotFound;
import com.vetcare.repository.OwnerRepository;
import com.vetcare.repository.PetRepository;
import java.time.LocalDate;
import java.util.List;

public class PetService {
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetService(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    public Pet register(long ownerId, String name, Species species, String breed, LocalDate birthDate) {
        ownerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFound("No existe el dueño con id " + ownerId));
        if (name == null || name.isBlank()) throw new BusinessRule("El nombre es obligatorio");
        if (species == null) throw new BusinessRule("Seleccione la especie");
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new BusinessRule("La fecha de nacimiento no puede ser futura");
        }
        return petRepository.save(new Pet(ownerId, name.trim(), species, breed, birthDate));
    }

    public Pet findById(long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("No existe la mascota con id " + id));
    }

    public List<Pet> findByOwner(long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public void update(Pet pet) {
        findById(pet.getId());
        if (pet.getName() == null || pet.getName().isBlank()) throw new BusinessRule("El nombre es obligatorio");
        if (pet.getSpecies() == null) throw new BusinessRule("Seleccione la especie");
        if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessRule("La fecha de nacimiento no puede ser futura");
        }
        petRepository.update(pet);
    }

    public void delete(long id) {
        findById(id);
        petRepository.deleteById(id);
    }
}