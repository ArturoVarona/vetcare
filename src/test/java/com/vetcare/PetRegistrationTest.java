package com.vetcare;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Pet;
import com.vetcare.domain.Species;
import com.vetcare.repository.jdbc.JdbcOwnerRepository;
import com.vetcare.repository.jdbc.JdbcPetRepository;
import com.vetcare.service.PetService;

class PetRegistrationTest {

    @Test
    void registraUnaMascotaEnSupabase() throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();
        PetService service = new PetService(
                new JdbcPetRepository(db),
                new JdbcOwnerRepository(db)
        );

        // Usa un owner que exista (el seed crea los owners con id 1 y 2)
        Pet pet = service.register(1, "PruebaJUnit", Species.DOG, "Mestizo", null);

        assertNotNull(pet.getId());
        System.out.println("Mascota creada con id " + pet.getId());
    }
}