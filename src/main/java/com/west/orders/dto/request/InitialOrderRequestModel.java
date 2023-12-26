package com.west.orders.dto.request;

import com.west.orders.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialOrderRequestModel {

    private List<OrderItemDto> cupcakes;

    private BigDecimal totalPrice;
}
