package com.vetcare;

import static org.junit.jupiter.api.Assertions.*;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Owner;
import com.vetcare.exception.EntityNotFound;
import com.vetcare.repository.jdbc.JdbcOwnerRepository;
import com.vetcare.repository.jdbc.JdbcPetRepository;
import com.vetcare.service.OwnerService;
import org.junit.jupiter.api.Test;

class OwnerCrudTest {

    @Test
    void crearLeerActualizarYEliminarUnDueno() throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();
        OwnerService service = new OwnerService(new JdbcOwnerRepository(db), new JdbcPetRepository(db));

        String doc = "TEST-" + System.currentTimeMillis();

        // CREATE
        Owner creado = service.register(doc, "Dueño de Prueba", "6000-0000", "prueba@test.com");
        assertNotNull(creado.getId());

        // READ
        assertEquals("Dueño de Prueba", service.findById(creado.getId()).getFullName());

        // UPDATE
        service.update(creado.getId(), doc, "Dueño Actualizado", "6111-1111", null);
        assertEquals("Dueño Actualizado", service.findById(creado.getId()).getFullName());

        // DELETE
        service.delete(creado.getId());
        assertThrows(EntityNotFound.class, () -> service.findById(creado.getId()));
    }
}