package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import com.west.orders.kafka.publisher.OrderRequestKafkaPublisher;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderReceivedEmailSender;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private CupcakeRepository cupcakeRepository;
    private OrderRequestKafkaPublisher orderRequestKafkaPublisher;
    private OrderReceivedEmailSender emailSender;

    public OrderResponseModel saveOrder(InitialOrderRequestModel customerOrder) {
//        TODO: validate

        List<OrderItem> cupcakes = convertToOrderItems(customerOrder, cupcakeRepository);

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .customerOrderRef(buildCustomerRef())
                .items(cupcakes)
                .totalPrice(customerOrder.getTotalPrice())
                .build();

        Order savedOrder = orderRepository.save(order);

        // build order kafka message and send
        DispatchOrder dispatchOrder = DispatchOrder.builder()
                .OrderId(order.getId())
                .dispatchStatus(DispatchStatus.PENDING)
                .build();

        try {
            orderRequestKafkaPublisher.process(dispatchOrder);
        } catch (Exception e) {
            log.error("Error while sending dispatch order message to kafka with order ID {}, error: {}",
                    order.getId(), e.getMessage());
        }

        emailSender.send(order);

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

    private Long buildCustomerRef() {
        SecureRandom rng = new SecureRandom();
        return 100000L + rng.nextInt(900000);
    }
}
