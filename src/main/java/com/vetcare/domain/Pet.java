package com.vetcare.domain;

import java.time.LocalDate;

public class Pet {

    private Long id;
    private final long ownerId;
    private final String name;
    private final Species species;
    private final String breed;
    private final LocalDate birthDate;

    // Para registrar una mascota nueva (aún sin id)
    public Pet(long ownerId, String name, Species species, String breed, LocalDate birthDate) {
        this(null, ownerId, name, species, breed, birthDate);
    }

    // Para reconstruir una mascota leída de la base
    public Pet(Long id, long ownerId, String name, Species species, String breed, LocalDate birthDate) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public Species getSpecies() { return species; }
    public String getBreed() { return breed; }
    public LocalDate getBirthDate() { return birthDate; }
}