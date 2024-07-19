package com.west.orders.validation.validator;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.exception.ValidationError;
import com.west.orders.validation.ValidationContext;
import com.west.orders.validation.ValidationContextDataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {

    @InjectMocks
    private OrderValidator validator;

    @Test
    public void shouldReturn_empty_ifNoValidationErrors() {
        List<String> serverProductCodes = List.of("CHOC001", "LEM001", "VAN001");
        List<OrderItemDto> orderItems = List.of(OrderItemDto.builder()
                        .productCode("CHOC001")
                        .count(3)
                        .build(),
                OrderItemDto.builder()
                        .productCode("LEM001")
                        .count(5)
                        .build());


        Map<ValidationContextDataType, Object> map = Map.of(
                ValidationContextDataType.PRODUCT_CODES, serverProductCodes,
                ValidationContextDataType.ORDER_ITEMS, orderItems
        );

        Optional<ValidationError> result = validator.validate(new ValidationContext(map));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturn_invalidCupcakeOrder_ifInvalidFlavour() {
        List<String> serverProductCodes = List.of("CHOC001", "LEM001", "VAN001");
        List<OrderItemDto> orderItems = List.of(OrderItemDto.builder()
                        .productCode("CHOC001")
                        .count(3)
                        .build(),
                OrderItemDto.builder()
                        .productCode("DNE001")
                        .count(5)
                        .build());

        Map<ValidationContextDataType, Object> map = Map.of(
                ValidationContextDataType.PRODUCT_CODES, serverProductCodes,
                ValidationContextDataType.ORDER_ITEMS, orderItems
        );

        String expectedError = "CUPCAKE_ORDER_ERROR";

        Optional<ValidationError> result = validator.validate(new ValidationContext(map));

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r.code()).isEqualTo(expectedError));
    }

    @Test
    public void shouldReturn_invalidCupcakeOrder_ifInvalidQuantity() {
        final int INVALID_QUANTITY = 0;

        List<String> serverProductCodes = List.of("CHOC001", "LEM001", "VAN001");
        List<OrderItemDto> orderItems = List.of(OrderItemDto.builder()
                        .productCode("CHOC001")
                        .count(3)
                        .build(),
                OrderItemDto.builder()
                        .productCode("LEM001")
                        .count(INVALID_QUANTITY)
                        .build());

        Map<ValidationContextDataType, Object> map = Map.of(
                ValidationContextDataType.PRODUCT_CODES, serverProductCodes,
                ValidationContextDataType.ORDER_ITEMS, orderItems
        );

        String expectedError = "CUPCAKE_ORDER_ERROR";

        Optional<ValidationError> result = validator.validate(new ValidationContext(map));

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r.code()).isEqualTo(expectedError));
    }
}