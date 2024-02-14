package com.west.orders.kafka.listener;


import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatchEventKafkaListener {

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
            // TODO: send order dispatched email
        } else {
            // TODO: handle alternative dispatch status scenarios
        }
    }
}
