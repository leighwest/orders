package com.west.orders.sqs.publisher;

import com.west.orders.sqs.message.DispatchOrder;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderRequestSqsPublisher {

    private final String orderCreatedQueue;
    private final SqsTemplate sqsTemplate;

    public OrderRequestSqsPublisher(
            @Value("${sqs.order-created-queue}") String orderCreatedQueue,
            SqsTemplate sqsTemplate) {
        this.orderCreatedQueue = orderCreatedQueue;
        this.sqsTemplate = sqsTemplate;
    }

    public void process(DispatchOrder dispatchOrder) {
        log.info("Sending order dispatch request SQS message for order ID: {}", dispatchOrder.getOrderId());
        sqsTemplate.send(orderCreatedQueue, dispatchOrder);
    }
}