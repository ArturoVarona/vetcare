package com.vetcare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Pet;
import com.vetcare.domain.Species;
import com.vetcare.exception.EntityNotFound;
import com.vetcare.repository.jdbc.JdbcOwnerRepository;
import com.vetcare.repository.jdbc.JdbcPetRepository;
import com.vetcare.service.PetService;

class PetCrudTest {

    @Test
    void crearLeerActualizarYEliminarUnaMascota() throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();
        PetService service = new PetService(new JdbcPetRepository(db), new JdbcOwnerRepository(db));

        // CREATE (el seed crea el dueño con id 1; ajusta si no existe)
        Pet creada = service.register(1, "PetCrudTest", Species.CAT, "Siamés", null);
        assertNotNull(creada.getId());

        // READ
        assertEquals("PetCrudTest", service.findById(creada.getId()).getName());

        // UPDATE
        service.update(new Pet(creada.getId(), 1, "PetCrudTest2", Species.CAT, "Persa", null));
        assertEquals("PetCrudTest2", service.findById(creada.getId()).getName());

        // DELETE
        service.delete(creada.getId());
        assertThrows(EntityNotFound.class, () -> service.findById(creada.getId()));
    }
}