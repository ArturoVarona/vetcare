package com.vetcare.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.vetcare.exception.BusinessRule;

public class Appointment {
    private Long id;
    private final long petId;
    private final long vetId;
    private final OffsetDateTime scheduledAt;
    private final String reason;
    private AppointmentStatus status;
    private BigDecimal fee;

    // Cita nueva
    public Appointment(long petId, long vetId, OffsetDateTime scheduledAt, String reason) {
        this(null, petId, vetId, scheduledAt, reason, AppointmentStatus.SCHEDULED, BigDecimal.ZERO);
    }

    // Cita leída de la base
    public Appointment(Long id, long petId, long vetId, OffsetDateTime scheduledAt,
                       String reason, AppointmentStatus status, BigDecimal fee) {
        this.id = id;
        this.petId = petId;
        this.vetId = vetId;
        this.scheduledAt = scheduledAt;
        this.reason = reason;
        this.status = status;
        this.fee = fee;
    }

    public void cancel() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new BusinessRule("Solo se puede cancelar una cita programada");
        }
        status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new BusinessRule("Solo se puede completar una cita programada");
        }
        status = AppointmentStatus.DONE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getPetId() { return petId; }
    public long getVetId() { return vetId; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public String getReason() { return reason; }
    public AppointmentStatus getStatus() { return status; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}