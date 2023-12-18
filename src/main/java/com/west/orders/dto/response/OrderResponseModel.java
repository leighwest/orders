package com.west.orders.dto.response;

import com.west.orders.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponseModel {

    private UUID id;

    private List<OrderItemDto> cupcakes;
}
