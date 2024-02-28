package com.west.orders.dto.response;

import com.west.orders.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "The definition of the Order Response")
public class OrderResponseModel {

    @Schema(description = "The order ID exposed to the client")
    private UUID id;

    @Schema(description = "List of order items")
    private List<OrderItemDto> cupcakes;
}
