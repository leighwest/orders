package com.west.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "The definition of an Order Item")
public class OrderItemDto {

    @Schema(description = "The product code exposed to the client", example = "CHOC001")
    private String productCode;

    @Schema(description = "The quantity of the given product ordered", example = "5")
    private int count;
}
