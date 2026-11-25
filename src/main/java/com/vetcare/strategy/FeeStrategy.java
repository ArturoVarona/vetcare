package com.vetcare.strategy;

import java.math.BigDecimal;

import com.vetcare.domain.Appointment;

public interface FeeStrategy {
    BigDecimal calculate(Appointment appointment);
}