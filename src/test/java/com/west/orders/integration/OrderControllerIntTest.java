package com.west.orders.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Order;
import com.west.orders.kafka.publisher.OrderRequestKafkaPublisher;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.EmailService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.west.orders.TestUtils.createInitialOrderRequestModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntTest extends AbstractionBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private OrderRequestKafkaPublisher orderRequestKafkaPublisher;

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("AWS_ACCESS_KEY", () -> "test-access-key");
        registry.add("AWS_SECRET_KEY", () -> "test-secret-key");
        registry.add("KAFKA_HOST", () -> "9999");
    }

    @Test
    public void shouldReturn_OrderResponseModel_whenCustomerSubmitsOrder() throws Exception {

        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

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

        verify(orderRequestKafkaPublisher, times(1)).process(any());

        verify(emailService, times(1)).sendEmail(any());
    }

    @Test
    public void shouldSave_Order_whenCustomerSubmitsOrder() throws Exception {
        InitialOrderRequestModel customerOrder = createInitialOrderRequestModel("VAN001");

        mockMvc.perform(post("/order")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerOrder)));

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);

        Order savedOrder = orders.get(0);

        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(28.00));
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getItems().get(0).getProductCode()).isEqualTo("CHOC001");
        assertThat(savedOrder.getItems().get(0).getCount()).isEqualTo(5);
        assertThat(savedOrder.getItems().get(1).getProductCode()).isEqualTo("VAN001");
        assertThat(savedOrder.getItems().get(1).getCount()).isEqualTo(3);
    }
}
