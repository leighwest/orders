package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CupcakeRepository cupcakeRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setup() {


    }

    @Test
    public void shouldReturn_orderDto_whenSaveOrder() {

        InitialOrderRequestModel expectedOrderDto = InitialOrderRequestModel.builder()
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

        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponseModel orderDto = orderService.saveOrder(expectedOrderDto);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getCupcakes().size()).isEqualTo(2);

        verify(cupcakeRepository, times(2)).findByProductCode(anyString());
        verify(orderRepository).save(any(Order.class));
    }

}