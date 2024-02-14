package com.west.orders.service.model;

import lombok.Data;

@Data
public class Mail {
    private final String to;
    private final String from;
    private final String subject;
    private final String content;
    private final Boolean isHtml;
}
