package com.west.orders.controller.exception;

import com.west.orders.exception.OrderServiceError;
import com.west.orders.exception.OrderServiceErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class NotFoundExceptionHandler {

    @ExceptionHandler(value= {EntityNotFoundException.class})
    @ResponseBody
    OrderServiceError handleNotFoundException(EntityNotFoundException exception) {
        log.error(exception.getMessage());
        return new OrderServiceError(OrderServiceErrorCode.PRODUCT_NOT_FOUND, exception.getMessage());
    }
}
