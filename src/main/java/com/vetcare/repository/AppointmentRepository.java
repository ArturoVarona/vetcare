package com.vetcare.repository;

import com.vetcare.domain.Appointment;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(long id);
    List<Appointment> findAll();
    List<Appointment> findByTimeSlot(OffsetDateTime scheduledAt);
    void update(Appointment appointment);
}