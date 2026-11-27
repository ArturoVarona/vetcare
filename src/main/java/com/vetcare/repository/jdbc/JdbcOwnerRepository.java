package com.vetcare.repository.jdbc;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Owner;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.OwnerRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcOwnerRepository implements OwnerRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcOwnerRepository.class.getName());
    private static final String COLUMNS = "id, document_id, full_name, phone, email";

    private final DatabaseConnection db;

    public JdbcOwnerRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Owner save(Owner owner) {
        String sql = "INSERT INTO owners (document_id, full_name, phone, email) "
                   + "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, owner.getDocumentId());
            ps.setString(2, owner.getFullName());
            ps.setString(3, owner.getPhone());
            ps.setString(4, owner.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                owner.setId(rs.getLong("id"));
                return owner;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar el dueño " + owner.getDocumentId(), e);
            throw new DataAccessException("No se pudo guardar el dueño", e);
        }
    }

    @Override
    public Optional<Owner> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM owners WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el dueño " + id, e);
            throw new DataAccessException("No se pudo consultar el dueño", e);
        }
    }

    @Override
    public Optional<Owner> findByDocumentId(String documentId) {
        String sql = "SELECT " + COLUMNS + " FROM owners WHERE document_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el dueño por documento " + documentId, e);
            throw new DataAccessException("No se pudo consultar el dueño", e);
        }
    }

    @Override
    public List<Owner> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM owners ORDER BY full_name";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Owner> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar dueños", e);
            throw new DataAccessException("No se pudo listar los dueños", e);
        }
    }

    @Override
    public void update(Owner owner) {
        String sql = "UPDATE owners SET document_id = ?, full_name = ?, phone = ?, email = ? "
                   + "WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, owner.getDocumentId());
            ps.setString(2, owner.getFullName());
            ps.setString(3, owner.getPhone());
            ps.setString(4, owner.getEmail());
            ps.setLong(5, owner.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar el dueño " + owner.getId(), e);
            throw new DataAccessException("No se pudo actualizar el dueño", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM owners WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar el dueño " + id, e);
            throw new DataAccessException("No se pudo eliminar el dueño", e);
        }
    }

    private Owner mapRow(ResultSet rs) throws SQLException {
        return new Owner(
                rs.getLong("id"),
                rs.getString("document_id"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("email"));
    }
}