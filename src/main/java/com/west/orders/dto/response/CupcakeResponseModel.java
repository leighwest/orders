package com.west.orders.dto.response;

import com.west.orders.entity.Cupcake;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class CupcakeResponseModel {

    private String productCode;

    private Cupcake.Flavour flavour;

    private BigDecimal price;

    private String image;
}
