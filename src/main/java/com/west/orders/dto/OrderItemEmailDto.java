package com.west.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemEmailDto {
    public final String name;
    public final int quantity;
    public final BigDecimal lineTotal; // unitPrice * count
    public final String imageUrl;
}
