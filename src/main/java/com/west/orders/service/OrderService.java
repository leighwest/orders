package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private CupcakeRepository cupcakeRepository;

    public OrderResponseModel saveOrder(InitialOrderRequestModel customerOrder) {
//        TODO: validate

        List<OrderItem> cupcakes = new ArrayList<>();

        customerOrder.getCupcakes().forEach(cupcake -> {
                    Cupcake cupcakeEntity = cupcakeRepository.findByProductCode(cupcake.getProductCode());
                    if (cupcakeEntity == null) {
                        throw new EntityNotFoundException("Cupcake not found for product code: " + cupcake.getProductCode());
                    }
                    cupcakes.add(OrderItem.builder()
                            .cupcakeId(cupcakeEntity.getId())
                            .productCode(cupcake.getProductCode())
                            .count(cupcake.getCount())
                            .build());
                }
        );

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .items(cupcakes).build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        savedOrder.getItems().forEach(cupcake ->
                orderItemDtos.add(OrderItemDto.builder()
                        .productCode(cupcake.getProductCode())
                        .count(cupcake.getCount())
                        .build()));

        return OrderResponseModel.builder()
                .id(savedOrder.getUuid())
                .cupcakes(orderItemDtos)
                .build();
    }
}
