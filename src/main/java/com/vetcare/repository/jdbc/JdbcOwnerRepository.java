package com.vetcare.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Owner;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.OwnerRepository;

public class JdbcOwnerRepository implements OwnerRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcOwnerRepository.class.getName());

    private final DatabaseConnection db;

    public JdbcOwnerRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Optional<Owner> findById(long id) {
        String sql = "SELECT id, document_id, full_name, phone, email FROM owners WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Owner(
                            rs.getLong("id"),
                            rs.getString("document_id"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("email")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el dueño " + id, e);
            throw new DataAccessException("No se pudo consultar el dueño", e);
        }
    }
}