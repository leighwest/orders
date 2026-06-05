package com.west.orders.service.notification.handler;

import com.west.orders.dto.OrderItemEmailDto;
import com.west.orders.entity.Order;
import com.west.orders.service.EmailService;
import com.west.orders.service.EmailTemplate;
import com.west.orders.service.model.Mail;
import com.west.orders.service.model.OrderDispatchedMailMetadata;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchedEmailSender {

    private final EmailService emailService;

    @Value("${cupcakes.images.base-url}")
    private String imagesBaseUrl;

    public void send(Order order) {
        Mail mail = getMail(order);

        try {
            emailService.sendEmail(mail);
        } catch (RuntimeException ex) {
            log.error("Error sending order dispatched email for order ID: {}. Error message: {}", order.getId(), ex.getMessage());
        } catch (MessagingException ex) {
            log.error("Error sending order dispatched email for order ID: {}. MessagingException: {}", order.getId(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private Mail getMail(Order order) {

        List<OrderItemEmailDto> items = order.getItems().stream()
                .map(item -> new OrderItemEmailDto(
                        item.getCupcake().getDisplayName(),
                        item.getCount(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getCount())),
                        imagesBaseUrl + "/" + item.getCupcake().getImage().getObjectKey()
                )).toList();

        OrderDispatchedMailMetadata metadata = new OrderDispatchedMailMetadata(
                order.getCustomer().getFirstName(),
                order.getCustomerOrderRef(),
                items,
                order.getTotalPrice()
        );

        String emailBody = emailService.getOrderEmail(
                EmailTemplate.ORDER_DISPATCHED_EMAIL.getTemplateName(),
                metadata
        );

        return new Mail(order.getCustomer().getEmail(), "cupcake-orders@leighwest.dev", "Your cupcakes are on their way!",
                emailBody, true);
    }
}