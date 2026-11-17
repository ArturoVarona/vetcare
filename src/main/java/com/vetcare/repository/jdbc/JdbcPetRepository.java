package com.vetcare.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Pet;
import com.vetcare.domain.Species;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.PetRepository;

public class JdbcPetRepository implements PetRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcPetRepository.class.getName());
    private static final String COLUMNS = "id, owner_id, name, species, breed, birth_date";

    private final DatabaseConnection db;

    public JdbcPetRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Pet save(Pet pet) {
        String sql = "INSERT INTO pets (owner_id, name, species, breed, birth_date) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, pet.getOwnerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies().name());
            ps.setString(4, pet.getBreed());
            ps.setObject(5, pet.getBirthDate());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                pet.setId(rs.getLong("id"));
                return pet;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la mascota " + pet.getName(), e);
            throw new DataAccessException("No se pudo guardar la mascota", e);
        }
    }

    @Override
    public Optional<Pet> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM pets WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar la mascota " + id, e);
            throw new DataAccessException("No se pudo consultar la mascota", e);
        }
    }

    @Override
    public List<Pet> findByOwnerId(long ownerId) {
        return query("SELECT " + COLUMNS + " FROM pets WHERE owner_id = ? ORDER BY name",
                ps -> ps.setLong(1, ownerId));
    }

    @Override
    public List<Pet> findByName(String name) {
        return query("SELECT " + COLUMNS + " FROM pets WHERE name ILIKE ? ORDER BY name",
                ps -> ps.setString(1, "%" + name + "%"));
    }

    @Override
    public void update(Pet pet) {
        String sql = "UPDATE pets SET owner_id = ?, name = ?, species = ?, breed = ?, "
                   + "birth_date = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, pet.getOwnerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies().name());
            ps.setString(4, pet.getBreed());
            ps.setObject(5, pet.getBirthDate());
            ps.setLong(6, pet.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la mascota " + pet.getId(), e);
            throw new DataAccessException("No se pudo actualizar la mascota", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM pets WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la mascota " + id, e);
            throw new DataAccessException("No se pudo eliminar la mascota", e);
        }
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Pet> query(String sql, Binder binder) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            binder.bind(ps);
            List<Pet> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al consultar mascotas", e);
            throw new DataAccessException("No se pudo consultar las mascotas", e);
        }
    }

    private Pet mapRow(ResultSet rs) throws SQLException {
        return new Pet(
                rs.getLong("id"),
                rs.getLong("owner_id"),
                rs.getString("name"),
                Species.valueOf(rs.getString("species")),
                rs.getString("breed"),
                rs.getObject("birth_date", LocalDate.class));
    }
}