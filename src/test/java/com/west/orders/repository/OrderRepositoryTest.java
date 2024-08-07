package com.west.orders.repository;

import com.west.orders.entity.Address;
import com.west.orders.entity.Customer;
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

import static com.west.orders.TestUtils.createAddress;
import static com.west.orders.TestUtils.createCustomer;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldReturn_savedOrder_whenSave() {

        Customer customer = createCustomer();

        Address address = createAddress();

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .customerOrderRef(21234412L)
                .customer(customer)
                .shippingAddress(address)
                .items(List.of(OrderItem.builder()
                        .productCode("CHOC001")
                        .cupcakeId(1L)
                        .count(5)
                        .unitPrice(BigDecimal.valueOf(3.5))
                        .build()))
                        .totalPrice(BigDecimal.valueOf(17.50))
                .build();

        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isPositive();
        assertThat(savedOrder.getCustomerOrderRef()).isEqualTo(21234412L);
        assertThat(savedOrder.getItems().size()).isEqualTo(1);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(BigDecimal.valueOf(17.50));
    }
}