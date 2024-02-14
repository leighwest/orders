package com.west.orders.service;

import com.west.orders.service.model.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;
    private SpringTemplateEngine templateEngine;

    public void sendEmail(Mail mail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, UTF_8.name());
        message.setFrom(mail.getFrom());
        message.setTo(mail.getTo());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent(), mail.getIsHtml());

        try {
            mailSender.send(mimeMessage);
        } catch (MailException ex) {
            throw new RuntimeException("Failed sending email: " + ex.getMessage(), ex);
        }
    }

    public <T> String getOrderEmail(String templateName, T metadata) {
        Context context = new Context();
        context.setVariable("metadata", metadata);

        String templatePath = "mail/" + templateName;

        return templateEngine.process(templatePath, context);
    }


}
