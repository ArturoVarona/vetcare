package com.vetcare.service;

import java.util.logging.Logger;

import com.vetcare.domain.Appointment;

public class LogReminderListener implements AppointmentListener {
    private static final Logger LOGGER = Logger.getLogger(LogReminderListener.class.getName());

    @Override
    public void onAppointmentUpcoming(Appointment appointment) {
        LOGGER.warning("Cita próxima: mascota " + appointment.getPetId()
                + " a las " + appointment.getScheduledAt());
    }
}