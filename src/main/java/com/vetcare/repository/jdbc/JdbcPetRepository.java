package com.vetcare.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vetcare.domain.Pet;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.PetRepository;

public class JdbcPetRepository implements PetRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcPetRepository.class.getName());
    private final Connection connection;

    public JdbcPetRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Pet save(Pet pet) {
        String sql = "INSERT INTO pet (client_id, name, species, breed, birth_date) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pet.getClientId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setObject(5, pet.getBirthDate());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                // Pet no tiene setId: devolvemos una mascota nueva con el id generado
                return new Pet(rs.getInt("id"), pet.getClientId(), pet.getName(),
                        pet.getSpecies(), pet.getBreed(), pet.getBirthDate());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la mascota " + pet.getName(), e);
            throw new DataAccessException("No se pudo guardar la mascota", e);
        }
    }

    @Override
    public List<Pet> findByClientId(int clientId) {
        String sql = "SELECT * FROM pet WHERE client_id = ? ORDER BY name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            List<Pet> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar mascotas del cliente " + clientId, e);
            throw new DataAccessException("No se pudo consultar las mascotas", e);
        }
    }

    // findById, findByName, update, deleteById
    @Override
    public Optional<Pet> findById(int id) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public List<Pet> findByName(String name) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public void update(Pet pet) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public void deleteById(int id) {
        throw new UnsupportedOperationException("Pendiente");
    }

    private Pet mapRow(ResultSet rs) throws SQLException {
        return new Pet(rs.getInt("id"), rs.getInt("client_id"), rs.getString("name"),
                rs.getString("species"), rs.getString("breed"),
                rs.getObject("birth_date", LocalDate.class));
    }
}