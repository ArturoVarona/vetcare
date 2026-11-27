package com.vetcare.service;

import com.vetcare.domain.Appointment;

public interface AppointmentListener {
    void onAppointmentUpcoming(Appointment appointment);
}