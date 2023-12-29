package com.west.orders.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.west.orders.kafka.message.PaymentOrder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDispatchService {

    private static final String ORDER_CREATED_TOPIC = "order.created";

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(PaymentOrder paymentOrder) throws Exception {
        log.info("Sending payment order request kafka message for order ID: {}", paymentOrder.getOrderId());
        kafkaProducer.send(ORDER_CREATED_TOPIC, paymentOrder).get();
    }
}
