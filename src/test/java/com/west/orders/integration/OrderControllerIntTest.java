package com.west.orders.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderControllerIntTest extends AbstractionBaseTest {

    @Autowired
    private MockMvc mockMvc;

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
                .totalPrice(BigDecimal.valueOf(32.00))
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

        ResultActions response = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerOrder)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cupcakes[0].productCode",
                        CoreMatchers.is(orderResponse.getCupcakes().get(0).getProductCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cupcakes[0].count",
                        CoreMatchers.is(orderResponse.getCupcakes().get(0).getCount())));
    }
}
