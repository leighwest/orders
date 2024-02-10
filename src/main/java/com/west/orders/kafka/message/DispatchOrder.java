package com.west.orders.kafka.message;

import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class DispatchOrder {

    public enum DispatchStatus {
        PENDING,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    private Long OrderId;

    private DispatchStatus dispatchStatus;
}
