package com.vetcare.repository.jdbc;

import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Appointment;
import com.vetcare.domain.AppointmentStatus;
import com.vetcare.exception.DataAccessException;
import com.vetcare.repository.AppointmentRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcAppointmentRepository implements AppointmentRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcAppointmentRepository.class.getName());
    private static final String COLUMNS = "id, pet_id, vet_id, scheduled_at, reason, status, fee";

    private final DatabaseConnection db;

    public JdbcAppointmentRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Appointment save(Appointment appointment) {
        String sql = "INSERT INTO appointments (pet_id, vet_id, scheduled_at, reason, status, fee) "
                   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, appointment.getPetId());
            ps.setLong(2, appointment.getVetId());
            ps.setObject(3, appointment.getScheduledAt());
            ps.setString(4, appointment.getReason());
            ps.setString(5, appointment.getStatus().name());
            ps.setBigDecimal(6, appointment.getFee());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                appointment.setId(rs.getLong("id"));
            }
            return appointment;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la cita", e);
            throw new DataAccessException("No se pudo guardar la cita", e);
        }
    }

    @Override
    public Optional<Appointment> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM appointments WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar la cita " + id, e);
            throw new DataAccessException("No se pudo consultar la cita", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM appointments ORDER BY scheduled_at";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Appointment> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar las citas", e);
            throw new DataAccessException("No se pudieron leer las citas", e);
        }
    }

    @Override
    public List<Appointment> findByTimeSlot(OffsetDateTime scheduledAt) {
        String sql = "SELECT " + COLUMNS + " FROM appointments "
                   + "WHERE scheduled_at = ? AND status = 'SCHEDULED'";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setObject(1, scheduledAt);
            List<Appointment> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar citas del horario " + scheduledAt, e);
            throw new DataAccessException("No se pudieron leer las citas", e);
        }
    }

    @Override
    public void update(Appointment appointment) {
        String sql = "UPDATE appointments SET scheduled_at = ?, reason = ?, status = ?, fee = ? "
                   + "WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setObject(1, appointment.getScheduledAt());
            ps.setString(2, appointment.getReason());
            ps.setString(3, appointment.getStatus().name());
            ps.setBigDecimal(4, appointment.getFee());
            ps.setLong(5, appointment.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar la cita " + appointment.getId(), e);
            throw new DataAccessException("No se pudo actualizar la cita", e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getLong("id"),
                rs.getLong("pet_id"),
                rs.getLong("vet_id"),
                rs.getObject("scheduled_at", OffsetDateTime.class),
                rs.getString("reason"),
                AppointmentStatus.valueOf(rs.getString("status")),
                rs.getBigDecimal("fee"));
    }
}