package com.vetcare.domain;

import java.time.LocalDate;

public class Pet {

    private int id;
    private int clientId;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    
    public Pet(int id, int clientId, String name, String species, String breed, LocalDate birthDate) {
    this.id = id;
    this.clientId = clientId;
    this.name = name;
    this.species = species;
    this.breed = breed;
    this.birthDate = birthDate;

    }

      public int getId() {
        return id;
    }

    public int getClientId() {
        return  clientId;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    
}
