package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.exception.ValidationError;
import com.west.orders.exception.ValidationException;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderReceivedEmailSender;
import com.west.orders.validation.OrderSubmissionValidationProcessor;
import com.west.orders.validation.ValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.west.orders.validation.validator.CupcakeErrorCode.CUPCAKE_ORDER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CupcakeRepository cupcakeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderSubmissionValidationProcessor validationProcessor;
    @Mock
    private OrderReceivedEmailSender emailSender;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setup() {


    }

    @Test
    public void shouldReturn_orderDto_whenSaveOrder() {

        InitialOrderRequestModel customerOrder = InitialOrderRequestModel.builder()
                .cupcakes(List.of(OrderItemDto.builder()
                                .productCode("CHOC001")
                                .count(5)
                                .build(),
                        OrderItemDto.builder()
                                .productCode("VAN001")
                                .count(3)
                                .build()
                ))
                .build();

        Cupcake chocolateCupcake = Cupcake.builder()
                .id(1L)
                .productCode("CHOC001")
                .flavour(Cupcake.Flavour.CHOCOLATE)
                .build();

        Cupcake vanillaCupcake = Cupcake.builder()
                .id(2L)
                .productCode("VAN001")
                .flavour(Cupcake.Flavour.VANILLA)
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .items(List.of(OrderItem.builder()
                                .productCode("CHOC001")
                                .cupcakeId(1L)
                                .count(5)
                                .build(),
                        OrderItem.builder()
                                .productCode("VAN001")
                                .cupcakeId(2L)
                                .count(3)
                                .build()))
                .build();

        when(cupcakeRepository.findByProductCode("CHOC001")).thenReturn(chocolateCupcake);
        when(cupcakeRepository.findByProductCode("VAN001")).thenReturn(vanillaCupcake);
        doNothing().when(emailSender).send(any());

        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponseModel orderDto = orderService.saveOrder(customerOrder);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getCupcakes().size()).isEqualTo(2);

        verify(cupcakeRepository, times(2)).findByProductCode(anyString());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void shouldThrow_error_ifCupcakeNotFound() {
        InitialOrderRequestModel customerOrder = InitialOrderRequestModel.builder()
                .cupcakes(List.of(OrderItemDto.builder()
                                .productCode("CHOC001")
                                .count(5)
                                .build(),
                        OrderItemDto.builder()
                                .productCode("DNE001")
                                .count(3)
                                .build()
                ))
                .build();

        List<String> productCodes = List.of("CHOC001", "LEM001", "VAN001");

        when(cupcakeRepository.findAllProductCodes()).thenReturn(productCodes);
        doThrow(new ValidationException(List.of(new ValidationError(CUPCAKE_ORDER_ERROR.getError(), "message")))).when(validationProcessor).validate(any(ValidationContext.class));

        assertThrows(ValidationException.class, () -> orderService.saveOrder(customerOrder));

        verify(cupcakeRepository, times(1)).findAllProductCodes();
        verifyNoInteractions(orderRepository);
    }
}