package com.west.orders.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.west.orders.kafka.message.PaymentOrder;

@Service
@RequiredArgsConstructor
public class PaymentDispatchService {

    private static final String ORDER_CREATED_TOPIC = "order.created";

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(PaymentOrder paymentOrder) throws Exception {

        kafkaProducer.send(ORDER_CREATED_TOPIC, paymentOrder).get();
    }
}
