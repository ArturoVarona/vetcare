package com.vetcare.strategy;

import com.vetcare.domain.Appointment;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class EmergencyFeeStrategy implements FeeStrategy {
    private final BigDecimal baseFee;
    private final BigDecimal surchargePercent;   // 50 = 50 %

    public EmergencyFeeStrategy(BigDecimal baseFee, BigDecimal surchargePercent) {
        this.baseFee = baseFee;
        this.surchargePercent = surchargePercent;
    }

    @Override
    public BigDecimal calculate(Appointment appointment) {
        BigDecimal surcharge = baseFee.multiply(surchargePercent).divide(BigDecimal.valueOf(100));
        return baseFee.add(surcharge).setScale(2, RoundingMode.HALF_UP);
    }
    
}