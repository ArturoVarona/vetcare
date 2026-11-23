package com.vetcare.repository.jdbc;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Role;
import com.vetcare.domain.User;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.UserRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcUserRepository implements UserRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcUserRepository.class.getName());
    private final DatabaseConnection db;

    public JdbcUserRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT id, username, full_name, role, active FROM app_users WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el usuario " + id, e);
            throw new DataAccessException("No se pudo consultar el usuario", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("full_name"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("active"));
    }
}