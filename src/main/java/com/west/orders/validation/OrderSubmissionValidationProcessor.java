package com.west.orders.validation;

import com.west.orders.validation.validator.OrderValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderSubmissionValidationProcessor extends ValidationProcessor {

    @Override
    void addValidators() {
        validators = List.of(new OrderValidator());
    }
}
