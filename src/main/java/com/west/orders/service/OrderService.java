package com.west.orders.service;

import com.west.orders.dto.OrderItemDto;
import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.entity.Cupcake;
import com.west.orders.entity.Order;
import com.west.orders.entity.OrderItem;
import com.west.orders.kafka.message.DispatchOrder;
import com.west.orders.kafka.message.DispatchOrder.DispatchStatus;
import com.west.orders.kafka.publisher.OrderRequestKafkaPublisher;
import com.west.orders.repository.CupcakeRepository;
import com.west.orders.repository.OrderRepository;
import com.west.orders.service.notification.handler.OrderReceivedEmailSender;
import com.west.orders.validation.OrderSubmissionValidationProcessor;
import com.west.orders.validation.ValidationContext;
import com.west.orders.validation.ValidationContextDataType;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private CupcakeRepository cupcakeRepository;
    private OrderSubmissionValidationProcessor validationProcessor;
    private OrderRequestKafkaPublisher orderRequestKafkaPublisher;
    private OrderReceivedEmailSender emailSender;

    public OrderResponseModel saveOrder(InitialOrderRequestModel customerOrder) {

        List<String> productCodes = cupcakeRepository.findAllProductCodes();

        Map<ValidationContextDataType, Object> map = Map.of(
                ValidationContextDataType.PRODUCT_CODES, productCodes,
                ValidationContextDataType.ORDER_ITEMS, customerOrder.getCupcakes()
        );

        validationProcessor.validate(new ValidationContext(map));

        List<OrderItem> cupcakes = convertToOrderItems(customerOrder, cupcakeRepository);

        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .customerOrderRef(buildCustomerRef())
                .items(cupcakes)
                .totalPrice(calculateOrderTotalPrice(cupcakes))
                .build();

        Order savedOrder = orderRepository.save(order);

        // build order kafka message and send
        DispatchOrder dispatchOrder = DispatchOrder.builder()
                .OrderId(order.getId())
                .dispatchStatus(DispatchStatus.PENDING)
                .build();

        try {
            orderRequestKafkaPublisher.process(dispatchOrder);
        } catch (Exception e) {
            log.error("Error while sending dispatch order message to kafka with order ID {}, error: {}",
                    order.getId(), e.getMessage());
        }

        emailSender.send(order);

        List<OrderItemDto> orderItemDtos = convertToOrderItemDtos(savedOrder);

        return OrderResponseModel.builder()
                .id(savedOrder.getUuid())
                .cupcakes(orderItemDtos)
                .build();
    }

    private List<OrderItem> convertToOrderItems(InitialOrderRequestModel customerOrder, CupcakeRepository cupcakeRepository) {
        return customerOrder.getCupcakes().stream().map(cupcakeRequest -> {
            Cupcake cupcakeEntity = cupcakeRepository.findByProductCode(cupcakeRequest.getProductCode());
            if (cupcakeEntity == null) {
                log.error("No cupcake found with product code: " + cupcakeRequest.getProductCode());
                throw new EntityNotFoundException("An internal error occurred. Please try again later.");
            }
            return OrderItem.builder()
                    .cupcakeId(cupcakeEntity.getId())
                    .productCode(cupcakeRequest.getProductCode())
                    .count(cupcakeRequest.getCount())
                    .unitPrice(cupcakeEntity.getUnitPrice())
                    .build();
        }).collect(Collectors.toList());
    }

    private List<OrderItemDto> convertToOrderItemDtos(Order savedOrder) {
        return savedOrder.getItems().stream().map(cupcake ->
             OrderItemDto.builder()
                    .productCode(cupcake.getProductCode())
                    .count(cupcake.getCount())
                    .build()
        ).collect(Collectors.toList());
    }

    private BigDecimal calculateOrderTotalPrice(List<OrderItem> cupcakes) {
        return cupcakes.stream().map(cupcake ->
                cupcake.getUnitPrice().multiply(BigDecimal.valueOf(cupcake.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long buildCustomerRef() {
        SecureRandom rng = new SecureRandom();
        return 100000L + rng.nextInt(900000);
    }
}
