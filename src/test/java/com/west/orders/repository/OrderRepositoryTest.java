package com.west.orders.repository;

import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldReturn_savedOrder_whenSave() {

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .items(List.of(OrderItem.builder()
                        .productCode("CHOC001")
                        .cupcakeId(1L)
                        .count(5)
                        .build()))
                        .totalPrice(BigDecimal.valueOf(32.00))
                .build();

        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isPositive();
        assertThat(savedOrder.getItems().size()).isEqualTo(1);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(BigDecimal.valueOf(32.00));
    }

}