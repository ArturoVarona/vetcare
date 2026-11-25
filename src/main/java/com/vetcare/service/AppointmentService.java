package com.vetcare.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vetcare.service.AppointmentService;
import com.vetcare.config.DatabaseConnection;
import com.vetcare.domain.Appointment;
import com.vetcare.exception.BusinessRule;
import com.vetcare.exception.DataAccessException;
import com.vetcare.exception.EntityNotFound;
import com.vetcare.repository.AppointmentRepository;
import com.vetcare.repository.PetRepository;
import com.vetcare.strategy.FeeStrategy;

public class AppointmentService {

    private static final Logger LOGGER = Logger.getLogger(AppointmentService.class.getName());

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final FeeStrategy feeStrategy;
    private final DatabaseConnection db;   // solo para controlar la transacción

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              FeeStrategy feeStrategy,
                              DatabaseConnection db) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.feeStrategy = feeStrategy;
        this.db = db;
    }

    /** Agenda una cita a futuro sin choque de horario con el mismo veterinario. */
    public Appointment schedule(long petId, long vetId, OffsetDateTime when, String reason) {
        petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFound("Mascota no encontrada: " + petId));
        if (when == null) throw new BusinessRule("La fecha y hora son obligatorias");
        if (reason == null || reason.isBlank()) throw new BusinessRule("El motivo es obligatorio");
        if (when.isBefore(OffsetDateTime.now())) {
            throw new BusinessRule("La cita debe ser en una fecha futura");
        }
        boolean ocupado = appointmentRepository.findByTimeSlot(when).stream()
                .anyMatch(a -> a.getVetId() == vetId);
        if (ocupado) {
            throw new BusinessRule("Ya hay una cita agendada en ese horario");
        }
        Appointment appointment = new Appointment(petId, vetId, when, reason.trim());
        appointmentRepository.save(appointment);
        LOGGER.info(() -> "Cita " + appointment.getId() + " agendada para " + when);
        return appointment;
    }

    public Appointment findById(long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Cita no encontrada: " + id));
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /** Cancela la cita y libera el horario. */
    public void cancel(long id) {
        Appointment appointment = findById(id);
        appointment.cancel();
        appointmentRepository.update(appointment);
        LOGGER.info(() -> "Cita " + id + " cancelada");
    }

    /**
     * Atención inmediata: guarda la cita, la marca como atendida y le pone el costo.
     * Las dos escrituras van en una transacción: o quedan las dos o ninguna.
     */
    public Appointment registerAndCharge(long petId, long vetId, String reason) {
        petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFound("Mascota no encontrada: " + petId));
        if (reason == null || reason.isBlank()) throw new BusinessRule("El motivo es obligatorio");

        Appointment appointment = new Appointment(petId, vetId, OffsetDateTime.now(), reason.trim());
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);                       // comienza la transacción
            appointmentRepository.save(appointment);
            appointment.complete();
            appointment.setFee(feeStrategy.calculate(appointment));
            appointmentRepository.update(appointment);
            conn.commit();                                   // ambas quedan confirmadas
            LOGGER.info(() -> "Cita " + appointment.getId() + " atendida y cobrada");
            return appointment;
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly(conn);                           // si algo falla, se deshace todo
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new DataAccessException("No se pudo registrar la cita", e);
        } finally {
            restoreAutoCommit(conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) return;
        try {
            conn.rollback();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "No se pudo hacer rollback", ex);
        }
    }

    private void restoreAutoCommit(Connection conn) {
        if (conn == null) return;
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "No se pudo restaurar el autocommit", ex);
        }
    }
}