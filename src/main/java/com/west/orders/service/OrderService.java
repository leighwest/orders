package com.west.orders.service;

import com.west.orders.dto.OrderDto;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;

    public OrderDto saveOrder(OrderDto orderDto) {
//        TODO: validate

    List<OrderItem> cupcakes = orderDto.getCupcakes();

    // transform OrderDto to Order
    Order order = Order.builder()
            .uuid(UUID.randomUUID())
            .items(cupcakes).build();


    Order savedOrder = orderRepository.save(order);

    // transform Order to OrderDto
    OrderDto savedOrderDto = OrderDto.builder()
            .id(savedOrder.getUuid())
            .cupcakes(savedOrder.getItems())
            .build();

    return savedOrderDto;


    }
}
