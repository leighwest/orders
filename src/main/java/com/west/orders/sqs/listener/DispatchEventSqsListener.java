package com.west.orders.sqs.listener;

import com.west.orders.entity.Order;
import com.west.orders.sqs.message.DispatchOrder;
import com.west.orders.sqs.message.DispatchOrder.DispatchStatus;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderDispatchedEmailSender;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatchEventSqsListener {

    private final OrderDispatchedEmailSender orderDispatchedEmailSender;
    private final OrderRepository orderRepository;

    @SqsListener("${sqs.order-dispatched-queue}")
    public void receive(DispatchOrder payload) {
        log.info("SQS message received with payload: {}", payload);

        if (payload.getDispatchStatus() == DispatchStatus.COMPLETED) {
            log.info("Received order successfully dispatched event for order ID: {}", payload.getOrderId());

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