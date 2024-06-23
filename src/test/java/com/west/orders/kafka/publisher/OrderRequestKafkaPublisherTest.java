package com.west.orders.kafka.publisher;

import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderRequestKafkaPublisherTest {

    private KafkaTemplate<String, Object> kafkaProducerMock;

    private OrderRequestKafkaPublisher orderRequestKafkaPublisher;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        orderRequestKafkaPublisher = new OrderRequestKafkaPublisher(kafkaProducerMock);
    }

    @Test
    public void process_Success() throws Exception {

        when(kafkaProducerMock.send(anyString(), anyString(), any(DispatchOrder.class)))
                .thenReturn(mock(CompletableFuture.class));

        DispatchOrder dispatchOrder = buildDispatchOrder();

        orderRequestKafkaPublisher.process(dispatchOrder);

        verify(kafkaProducerMock, times(1)).send(eq("order.created"), any(), any(DispatchOrder.class));
    }

    @Test
    public void shouldThrowException_when_orderCreatedProducerFails() {
        DispatchOrder dispatchOrder = buildDispatchOrder();

        doThrow(new RuntimeException("order created producer failure")).when(kafkaProducerMock).send(eq("order.created"), anyString(), any(DispatchOrder.class));

        Exception exception = assertThrows(RuntimeException.class, () -> orderRequestKafkaPublisher.process(dispatchOrder));

        verify(kafkaProducerMock, times(1)).send(eq("order.created"), anyString(), any(DispatchOrder.class));
        assertThat(exception.getMessage(), equalTo("order created producer failure"));
    }

    private DispatchOrder buildDispatchOrder() {
        return DispatchOrder.builder()
                .OrderId(1L)
                .dispatchStatus(DispatchStatus.PENDING)
                .build();
    }
}