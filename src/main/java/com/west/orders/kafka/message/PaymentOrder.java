package com.west.orders.kafka.message;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentOrder {

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        CANCELED,
        FAILED
    }

    private final Long OrderId;

    BigDecimal totalPrice;

    PaymentStatus paymentStatus;
}
