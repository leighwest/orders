package com.west.orders.validation.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CupcakeErrorCode {
    CUPCAKE_ORDER_ERROR("CUPCAKE_ORDER_ERROR");

    private final String error;
}
