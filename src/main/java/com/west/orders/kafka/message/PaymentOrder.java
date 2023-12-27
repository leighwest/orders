package com.west.orders.kafka.message;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
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
