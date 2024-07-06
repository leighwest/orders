package com.west.orders.kafka.listener;

import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderDispatchedEmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchEventKafkaListenerTest {

    @Mock
    private OrderDispatchedEmailSender orderDispatchedEmailSender;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DispatchEventKafkaListener dispatchEventKafkaListener;

    @Test
    public void process_Success() {

        Optional<Order> order = Optional.ofNullable(buildOrder());

        when(orderRepository.findById(anyLong())).thenReturn(order);
        doNothing().when(orderDispatchedEmailSender).send(any(Order.class));

        dispatchEventKafkaListener.receive(buildDispatchOrder(DispatchOrder.DispatchStatus.COMPLETED));

        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderDispatchedEmailSender, times(1)).send(any());
    }

    @Test
    public void shouldNotCall_OrderDispatchedEmailSender_ifOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        dispatchEventKafkaListener.receive(buildDispatchOrder(DispatchOrder.DispatchStatus.COMPLETED));

        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderDispatchedEmailSender, never()).send(any());
    }

    @Test
    public void shouldNotCall_OrderDispatchedEmailSender_ifDispatchStatusNotCompleted() {

        dispatchEventKafkaListener.receive(buildDispatchOrder(DispatchOrder.DispatchStatus.PENDING));

        verify(orderRepository, never()).findById(anyLong());
        verify(orderDispatchedEmailSender, never()).send(any());
    }

    private DispatchOrder buildDispatchOrder(DispatchOrder.DispatchStatus status) {
        return DispatchOrder.builder()
                .OrderId(1L)
                .dispatchStatus(status)
                .build();
    }

    private Order buildOrder() {
        return Order.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .customerOrderRef(21234412L)
                .items(List.of(OrderItem.builder()
                        .productCode("CHOC001")
                        .cupcakeId(1L)
                        .count(5)
                        .build()))
                .totalPrice(BigDecimal.valueOf(32.00))
                .build();
    }
}