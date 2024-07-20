package com.west.orders.service.notification.handler;

import com.west.orders.entity.Order;
import com.west.orders.service.EmailService;
import com.west.orders.service.EmailTemplate;
import com.west.orders.service.model.Mail;
import com.west.orders.service.model.OrderReceivedMailMetadata;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class OrderReceivedEmailSender {

    private final EmailService emailService;

    public void send(Order order) {
        Mail mail = getMail(order);

        try {
            emailService.sendEmail(mail);
        } catch (RuntimeException ex) {
            log.error("Error sending order received email for order ID: {}. Error message: {}", order.getId(), ex.getMessage());
        } catch (MessagingException ex) {
            log.error("Error sending order received email for order ID: {}. MessagingException: {}", order.getId(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private Mail getMail(Order order) {

        OrderReceivedMailMetadata metadata = new OrderReceivedMailMetadata(
                order.getCustomer().getFirstName(), order.getCustomerOrderRef()
        );

        String emailBody = emailService.getOrderEmail(
                EmailTemplate.ORDER_RECEIVED_EMAIL.getTemplateName(),
                metadata
        );

        return new Mail(order.getCustomer().getEmail(), "cupcake-orders@leighwest.dev", "We've got your cupcake order!",
                emailBody, true);
    }
}
