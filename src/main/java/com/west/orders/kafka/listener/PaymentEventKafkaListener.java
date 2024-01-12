package com.west.orders.kafka.listener;


import com.west.orders.kafka.message.PaymentOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentEventKafkaListener {

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "payment.processed",
            groupId = "payment.processed.consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void receive(@Payload PaymentOrder payload) {
        log.info("Kafka broker received message with payload: {}", payload);

        if (payload.getPaymentStatus() == PaymentOrder.PaymentStatus.COMPLETED) {
            log.info("Received payment processed event for order id: {}", payload.getOrderId());
//            paymentService.completePayment(payload);
        } else {
            // TODO: handle alternative payment status scenarios
        }
    }
}
