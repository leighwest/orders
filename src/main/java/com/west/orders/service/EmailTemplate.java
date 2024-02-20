package com.west.orders.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {
    ORDER_RECEIVED_EMAIL("email-order-received.html"),
    ORDER_DISPATCHED_EMAIL("email-order-dispatched.html");

    private final String templateName;
}
