package com.west.orders.controller;

import com.west.orders.dto.request.InitialOrderRequestModel;
import com.west.orders.dto.response.OrderResponseModel;
import com.west.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Submit an order for cupcakes")
    @PostMapping("/order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order submitted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content) })
    public ResponseEntity<OrderResponseModel> createOrder(@RequestBody InitialOrderRequestModel orderDetails) {

        OrderResponseModel savedOrder = orderService.saveOrder(orderDetails);

        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }
}
