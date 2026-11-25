package com.vetcare.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vetcare.domain.Appointment;
import com.vetcare.domain.AppointmentStatus;

public class AppointmentReminderChecker {
    private final List<AppointmentListener> listeners = new ArrayList<>();

    public void subscribe(AppointmentListener listener) {
        listeners.add(listener);
    }

    /** Avisa de las citas programadas que ocurren dentro de la ventana indicada. */
    public void check(List<Appointment> appointments, OffsetDateTime now, Duration window) {
        for (Appointment a : appointments) {
            boolean pending = a.getStatus() == AppointmentStatus.SCHEDULED;
            boolean soon = !a.getScheduledAt().isBefore(now)
                    && a.getScheduledAt().isBefore(now.plus(window));
            if (pending && soon) {
                listeners.forEach(l -> l.onAppointmentUpcoming(a));
            }
        }
    }
}