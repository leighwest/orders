package com.west.orders.dto;

import com.west.orders.entity.Cupcake;
import com.west.orders.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class OrderDto {

    private UUID id;

    private List<OrderItem> cupcakes;
}
