package com.west.orders.service;

import com.west.orders.service.model.OrderReceivedMailMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmailServiceIntTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void shouldGenerateOrderReceivedEmail() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        String expectedContent;

        try (InputStream inputStream = classLoader.getResourceAsStream("testEmails/email-order-received.html")) {
            if (inputStream == null) {
                throw new AssertionError("File not found in resources.");
            }
            expectedContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        OrderReceivedMailMetadata metadata = new OrderReceivedMailMetadata(
                "Leigh", 1234L
        );

        String htmlTemplate = emailService.getOrderEmail(EmailTemplate.ORDER_RECEIVED_EMAIL.getTemplateName(), metadata);

        assertThat(expectedContent.trim()).isEqualTo(htmlTemplate.trim());
    }
}
