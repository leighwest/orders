package com.west.orders.sqs.publisher;

import com.west.orders.sqs.message.DispatchOrder;
import com.west.orders.sqs.message.DispatchOrder.DispatchStatus;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderRequestSqsPublisherTest {

    private SqsTemplate sqsTemplateMock;
    private OrderRequestSqsPublisher orderRequestSqsPublisher;

    private static final String ORDER_CREATED_QUEUE = "order-created";

    @BeforeEach
    void setUp() {
        sqsTemplateMock = mock(SqsTemplate.class);
        orderRequestSqsPublisher = new OrderRequestSqsPublisher(ORDER_CREATED_QUEUE, sqsTemplateMock);
    }

    @Test
    public void process_Success() {
        DispatchOrder dispatchOrder = buildDispatchOrder();

        orderRequestSqsPublisher.process(dispatchOrder);

        verify(sqsTemplateMock, times(1)).send(eq(ORDER_CREATED_QUEUE), eq(dispatchOrder));
    }

    @Test
    public void shouldThrowException_when_sqsPublisherFails() {
        DispatchOrder dispatchOrder = buildDispatchOrder();

        doThrow(new RuntimeException("SQS publish failure"))
                .when(sqsTemplateMock).send(eq(ORDER_CREATED_QUEUE), any(DispatchOrder.class));

        Exception exception = assertThrows(RuntimeException.class,
                () -> orderRequestSqsPublisher.process(dispatchOrder));

        verify(sqsTemplateMock, times(1)).send(eq(ORDER_CREATED_QUEUE), any(DispatchOrder.class));
        assertThat(exception.getMessage(), equalTo("SQS publish failure"));
    }

    private DispatchOrder buildDispatchOrder() {
        return DispatchOrder.builder()
                .OrderId(1L)
                .dispatchStatus(DispatchStatus.PENDING)
                .build();
    }
}