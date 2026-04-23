package com.west.orders.sqs.message;

import lombok.*;

@Builder
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
