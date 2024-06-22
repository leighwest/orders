package com.west.orders.validation.validator;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.exception.ValidationError;
import com.west.orders.validation.ValidationContext;
import com.west.orders.validation.ValidationContextDataType;

import java.util.List;
import java.util.Optional;

import static com.west.orders.validation.validator.CupcakeErrorCode.CUPCAKE_ORDER_ERROR;

public class OrderValidator implements Validator{
    @Override
    public Optional<ValidationError> validate(ValidationContext context) {
        @SuppressWarnings("unchecked")
        List<String> serverProductCodes = (List<String>) context.getData().get(ValidationContextDataType.PRODUCT_CODES);
        List<OrderItemDto> orderItems = (List<OrderItemDto>) context.getData().get(ValidationContextDataType.ORDER_ITEMS);

        for (OrderItemDto orderItem : orderItems) {
            if (!isValidProductCode(orderItem.getProductCode(), serverProductCodes)) {
                return Optional.of(new ValidationError(CUPCAKE_ORDER_ERROR.getError(), "Invalid cupcake flavour: " +
                        orderItem.getProductCode()));
            }

            if (orderItem.getCount() < 1) {
                return Optional.of(new ValidationError(CUPCAKE_ORDER_ERROR.getError(), "Invalid cupcake quantity. " +
                        "Quantity must be at least 1"));
            }
        }
        return Optional.empty();
    }

    private boolean isValidProductCode(String productCode, List<String> serverProductCodes) {
        for (String serverProductCode : serverProductCodes) {
            if (serverProductCode.toUpperCase().charAt(0) > productCode.toUpperCase().charAt(0)) {
                return false;
            }
            if (productCode.equals(serverProductCode)) {
                return true;
            }
        }
        return false;
    }
}
