package com.west.orders.repository;

import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
                .build();

        Order savedOrder = orderRepository.save(order);

        Assertions.assertThat(savedOrder).isNotNull();
        Assertions.assertThat(savedOrder.getId()).isPositive();
        Assertions.assertThat(savedOrder.getItems().size()).isEqualTo(1);
    }

}