package com.west.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.service.OrderService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturn_OrderResponseModel_whenCustomerSubmitsOrder() throws Exception {

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

        OrderResponseModel orderResponse = OrderResponseModel.builder()
                .id(UUID.randomUUID())
                .cupcakes(List.of(OrderItemDto.builder()
                                .productCode("CHOC001")
                                .count(5)
                                .build(),
                        OrderItemDto.builder()
                                .productCode("VAN001")
                                .count(3)
                                .build()
                )).build();

        when(orderService.saveOrder(any(InitialOrderRequestModel.class))).thenReturn(orderResponse);

        ResultActions response = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOrder)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id",
                        CoreMatchers.is(orderResponse.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cupcakes[0].productCode",
                        CoreMatchers.is(orderResponse.getCupcakes().get(0).getProductCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cupcakes[0].count",
                        CoreMatchers.is(orderResponse.getCupcakes().get(0).getCount())));

        verify(orderService).saveOrder(any(InitialOrderRequestModel.class));
    }
}