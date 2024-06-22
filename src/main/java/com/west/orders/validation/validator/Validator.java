package com.west.orders.validation.validator;

import com.west.orders.exception.ValidationError;
import com.west.orders.validation.ValidationContext;

import java.util.Optional;

public interface Validator {
    Optional<ValidationError> validate(ValidationContext context);
}
