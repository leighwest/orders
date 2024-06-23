package com.west.orders.dto.request;

import com.west.orders.dto.CustomerDto;
import com.west.orders.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "The definition of the Initial Order")
public class InitialOrderRequestModel {

    @Schema(description =  "Customer details")
    private CustomerDto customer;

    @Schema(description = "List of order items")
    private List<OrderItemDto> cupcakes;
}
