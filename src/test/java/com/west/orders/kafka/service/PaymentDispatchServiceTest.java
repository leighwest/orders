package com.west.orders.kafka.service;

import com.west.orders.kafka.message.PaymentOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class PaymentDispatchServiceTest {

    private KafkaTemplate<String, Object> kafkaProducerMock;

    private PaymentDispatchService paymentDispatchService;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        paymentDispatchService = new PaymentDispatchService(kafkaProducerMock);
    }

    @Test
    public void process_Success() throws Exception {

        when(kafkaProducerMock.send(anyString(), any(PaymentOrder.class)))
                .thenReturn(mock(CompletableFuture.class));

        PaymentOrder paymentOrder = buildPaymentOrder();

        paymentDispatchService.process(paymentOrder);

        verify(kafkaProducerMock, times(1)).send(eq("order.created"), any(PaymentOrder.class));
    }

    @Test
    public void shouldThrowException_when_orderCreatedFailsProducerFails() {
        PaymentOrder paymentOrder = buildPaymentOrder();

        when(kafkaProducerMock.send(anyString(), any(PaymentOrder.class))).thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException("order created producer failure")).when(kafkaProducerMock).send(eq("order.created"), any(PaymentOrder.class));

        Exception exception = assertThrows(RuntimeException.class, () -> paymentDispatchService.process(paymentOrder));

        verify(kafkaProducerMock, times(1)).send(eq("order.created"), any(PaymentOrder.class));
        assertThat(exception.getMessage(), equalTo("order created producer failure"));
    }

    private PaymentOrder buildPaymentOrder() {
        return PaymentOrder.builder()
                .OrderId(1L)
                .totalPrice(BigDecimal.valueOf(16.99))
                .paymentStatus(PaymentOrder.PaymentStatus.PENDING)
                .build();
    }
}