package com.vetcare.strategy;

import com.vetcare.domain.Appointment;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardFeeStrategy implements FeeStrategy {
    private final BigDecimal baseFee;

    public StandardFeeStrategy(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    @Override
    public BigDecimal calculate(Appointment appointment) {
        return baseFee.setScale(2, RoundingMode.HALF_UP);
    }
}