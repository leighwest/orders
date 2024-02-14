package com.west.orders.dto.response;

import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DispatchResponseModel {

    private Long id;

    private DispatchStatus dispatchStatus;
}
