package com.west.orders.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ValidationContext {

    private final Map<ValidationContextDataType, Object> data;
}
