package com.west.orders.service;

import com.west.orders.service.model.OrderReceivedMailMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    SpringTemplateEngine templateEngine;

    @InjectMocks
    EmailService emailService;

    @Test
    public void getOrderEmail_shouldUseCorrect_templatePath() {

        String expectedOutput = "mock html template";
        String expectedPath = "mail/email-order-received.html";
        String templateName = EmailTemplate.ORDER_RECEIVED_EMAIL.getTemplateName();
        OrderReceivedMailMetadata metadata = new OrderReceivedMailMetadata(
                "Leigh", 1234L
        );

        Context context = mock(Context.class);
        context.setVariable("metadata", metadata);

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedOutput);

        String emailContent = emailService.getOrderEmail(
                templateName,
                context);

        assertEquals(expectedOutput, emailContent);

        verify(templateEngine).process(eq(expectedPath), any(Context.class));
    }

}