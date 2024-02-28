package com.west.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "The definition of an Order Item")
public class OrderItemDto {

    @Schema(description = "The product code exposed to the client")
    private String productCode;

    @Schema(description = "The quantity of the given product ordered")
    private int count;
}
