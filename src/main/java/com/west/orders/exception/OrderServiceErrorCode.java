package com.west.orders.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderServiceErrorCode {
    PRODUCT_NOT_FOUND("product_not_found");

    private final String error;
}
