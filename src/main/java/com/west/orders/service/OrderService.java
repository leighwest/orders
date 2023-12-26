package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.kafka.message.PaymentOrder;
import com.west.orders.kafka.service.PaymentDispatchService;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private CupcakeRepository cupcakeRepository;
    private PaymentDispatchService paymentDispatchService;

    public OrderResponseModel saveOrder(InitialOrderRequestModel customerOrder) {
//        TODO: validate

        List<OrderItem> cupcakes = convertToOrderItems(customerOrder, cupcakeRepository);

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .items(cupcakes)
                .totalPrice(customerOrder.getTotalPrice())
                .build();

        Order savedOrder = orderRepository.save(order);

        // build order kafka message and send
        PaymentOrder paymentOrder = PaymentOrder.builder()
                .OrderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .paymentStatus(PaymentOrder.PaymentStatus.PENDING)
                .build();

        try {
            paymentDispatchService.process(paymentOrder);
        } catch (Exception e) {
            log.error("Error while sending payment order message to kafka with order ID {}, error: {}",
                    order.getId(), e.getMessage());
        }

        List<OrderItemDto> orderItemDtos = convertToOrderItemDtos(savedOrder);

        return OrderResponseModel.builder()
                .id(savedOrder.getUuid())
                .cupcakes(orderItemDtos)
                .build();
    }

    private List<OrderItem> convertToOrderItems(InitialOrderRequestModel customerOrder, CupcakeRepository cupcakeRepository) {
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
        });

        return cupcakes;
    }

    private List<OrderItemDto> convertToOrderItemDtos(Order savedOrder) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        savedOrder.getItems().forEach(cupcake ->
                orderItemDtos.add(OrderItemDto.builder()
                        .productCode(cupcake.getProductCode())
                        .count(cupcake.getCount())
                        .build()));

        return orderItemDtos;
    }
}
