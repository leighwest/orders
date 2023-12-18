package com.west.orders.dto.request;

import com.west.orders.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class InitialOrderRequestModel {

    private List<OrderItemDto> cupcakes;
}
