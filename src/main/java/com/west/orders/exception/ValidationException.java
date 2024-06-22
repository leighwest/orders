package com.west.orders.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ValidationException extends RuntimeException {
    private List<ValidationError> errors;

    List<ValidationError> getErrors() {
        return errors;
    }
}
