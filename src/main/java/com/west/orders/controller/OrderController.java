package com.west.orders.controller;

import com.west.orders.dto.OrderDto;
import com.west.orders.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController // combines @Controller and @ResponseBody annotations: ResponseBody: tells controller that the object
                // returned is automatically serialized into JSON and passed back into the HttpResponse object
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDetails) {

        OrderDto savedOrder = orderService.saveOrder(orderDetails);

        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }
}
