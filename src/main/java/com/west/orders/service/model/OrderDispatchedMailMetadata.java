package com.west.orders.service.model;

import lombok.Data;

@Data
public class OrderDispatchedMailMetadata {

    public final String givenName;

    public final Long orderRef;
}
