package com.west.orders.service.model;

import com.west.orders.dto.OrderItemEmailDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDispatchedMailMetadata {

    public final String givenName;

    public final Long orderRef;

    public final List<OrderItemEmailDto> items;

    public final BigDecimal total;


}
