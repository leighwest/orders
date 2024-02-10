package com.west.orders.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.west.orders.kafka.message.DispatchOrder;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestKafkaPublisher {

    private static final String ORDER_CREATED_TOPIC = "order.created";

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(DispatchOrder dispatchOrder) throws Exception {
        log.info("Sending order dispatch request kafka message for order ID: {}", dispatchOrder.getOrderId());
        kafkaProducer.send(ORDER_CREATED_TOPIC, dispatchOrder).get();
    }
}
