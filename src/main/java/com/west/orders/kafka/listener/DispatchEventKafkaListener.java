package com.west.orders.kafka.listener;


import com.west.orders.entity.Order;
import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderDispatchedEmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatchEventKafkaListener {

    private final OrderDispatchedEmailSender orderDispatchedEmailSender;
    private final OrderRepository orderRepository;

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "dispatch.processed",
            groupId = "dispatch.processed.consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void receive(@Payload DispatchOrder payload) {
        log.info("Kafka broker received message with payload: {}", payload);

        if (payload.getDispatchStatus() == DispatchStatus.COMPLETED) {
            log.info("Received order successfully dispatched event for order id: {}", payload.getOrderId());

            Optional<Order> order = orderRepository.findById(payload.getOrderId());

            if (order.isPresent()) {
                orderDispatchedEmailSender.send(order.get());
            } else {
                log.error("Unable to retrieve order with order ID: {}", payload.getOrderId());
            }
        } else {
            // TODO: handle alternative dispatch status scenarios
        }
    }
}
