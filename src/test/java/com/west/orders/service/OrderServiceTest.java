package com.west.orders.service;

import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.*;
import com.west.orders.exception.ValidationError;
import com.west.orders.exception.ValidationException;
import com.west.orders.repository.AddressRepository;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderReceivedEmailSender;
import com.west.orders.validation.OrderSubmissionValidationProcessor;
import com.west.orders.validation.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.west.orders.TestUtils.*;
import static com.west.orders.validation.validator.CupcakeErrorCode.CUPCAKE_ORDER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private CupcakeRepository cupcakeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderSubmissionValidationProcessor validationProcessor;
    @Mock
    private OrderReceivedEmailSender emailSender;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldReturn_orderDto_whenSaveOrder() {

        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

        Customer customer = createCustomer();

        Address address = createAddress();

        Cupcake chocolateCupcake = Cupcake.builder()
                .id(1L)
                .productCode("CHOC001")
                .flavour(Cupcake.Flavour.CHOCOLATE)
                .unitPrice(BigDecimal.valueOf(3.5))
                .build();

        Cupcake vanillaCupcake = Cupcake.builder()
                .id(2L)
                .productCode("VAN001")
                .flavour(Cupcake.Flavour.VANILLA)
                .unitPrice(BigDecimal.valueOf(3.00))
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .customer(customer)
                .shippingAddress(address)
                .items(List.of(OrderItem.builder()
                                .productCode("CHOC001")
                                .cupcakeId(1L)
                                .count(5)
                                .unitPrice(BigDecimal.valueOf(3.50))
                                .build(),
                        OrderItem.builder()
                                .productCode("VAN001")
                                .cupcakeId(2L)
                                .count(3)
                                .unitPrice(BigDecimal.valueOf(3.00))
                                .build()))
                .build();

        when(cupcakeRepository.findByProductCode("CHOC001")).thenReturn(chocolateCupcake);
        when(cupcakeRepository.findByProductCode("VAN001")).thenReturn(vanillaCupcake);
        doNothing().when(emailSender).send(any());

        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponseModel orderDto = orderService.saveOrder(customerOrder);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getCupcakes().size()).isEqualTo(2);

        verify(customerService).findOrSaveCustomer(any(InitialOrderRequestModel.class));
        verify(cupcakeRepository, times(2)).findByProductCode(anyString());
        verify(addressRepository).save(any(Address.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void shouldThrow_error_ifCupcakeNotFound() {
        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("DNE001");

        List<String> productCodes = List.of("CHOC001", "LEM001", "VAN001");

        when(cupcakeRepository.findAllProductCodes()).thenReturn(productCodes);
        doThrow(new ValidationException(List.of(new ValidationError(CUPCAKE_ORDER_ERROR.getError(), "message")))).when(validationProcessor).validate(any(ValidationContext.class));

        assertThrows(ValidationException.class, () -> orderService.saveOrder(customerOrder));

        verify(cupcakeRepository, times(1)).findAllProductCodes();
        verifyNoInteractions(orderRepository);
    }

    @ParameterizedTest
    @MethodSource("provideValuesForOrderPriceVerifier")
    public void shouldCorrectly_calculate_orderTotal(BigDecimal chocCupcakeUnitPrice,
                                                     BigDecimal vanCupcakeUnitPrice,
                                                     BigDecimal expectedOrderTotal) {

        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

        Cupcake chocolateCupcake = Cupcake.builder()
                .id(1L)
                .productCode("CHOC001")
                .flavour(Cupcake.Flavour.CHOCOLATE)
                .unitPrice(chocCupcakeUnitPrice)
                .build();

        Cupcake vanillaCupcake = Cupcake.builder()
                .id(2L)
                .productCode("VAN001")
                .flavour(Cupcake.Flavour.VANILLA)
                .unitPrice(vanCupcakeUnitPrice)
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .items(List.of(OrderItem.builder()
                                .productCode("CHOC001")
                                .cupcakeId(1L)
                                .count(5)
                                .unitPrice(chocCupcakeUnitPrice)
                                .build(),
                        OrderItem.builder()
                                .productCode("VAN001")
                                .cupcakeId(2L)
                                .count(3)
                                .unitPrice(vanCupcakeUnitPrice)
                                .build()))
                .build();

        when(cupcakeRepository.findByProductCode("CHOC001")).thenReturn(chocolateCupcake);
        when(cupcakeRepository.findByProductCode("VAN001")).thenReturn(vanillaCupcake);
        doNothing().when(emailSender).send(any());
        when(orderRepository.save(any())).thenReturn(savedOrder);

        orderService.saveOrder(customerOrder);

        // Capture the Order object
        ArgumentCaptor<Order> orderCaptor = forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        assertThat(expectedOrderTotal).isEqualTo(capturedOrder.getTotalPrice());
    }

    private static Stream<Arguments> provideValuesForOrderPriceVerifier() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(3.5), BigDecimal.valueOf(3.00), BigDecimal.valueOf(26.50)),
                Arguments.of(BigDecimal.valueOf(3.9), BigDecimal.valueOf(3.15), BigDecimal.valueOf(28.95)),
                Arguments.of(BigDecimal.valueOf(3.75), BigDecimal.valueOf(3.50), BigDecimal.valueOf(29.25))
        );
    }
}