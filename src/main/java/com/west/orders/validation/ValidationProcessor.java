package com.west.orders.validation;

import com.west.orders.exception.ValidationError;
import com.west.orders.exception.ValidationException;
import com.west.orders.validation.validator.Validator;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
abstract class ValidationProcessor {

    protected List<Validator> validators = new ArrayList<>();
    private List<ValidationError> validationErrors = new ArrayList<>();

    ValidationProcessor() {
        this.addValidators();
        if (validators.isEmpty()) {
            throw new IllegalStateException("There is no validator defined in the Validation Processor");
        }
    }

    abstract void addValidators();

    public void validate(ValidationContext context) {
        validators.forEach(validator -> {
            Optional<ValidationError> error = validator.validate(context);
            error.ifPresent(validationError -> validationErrors.add(validationError));
        });

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }
    }
}
