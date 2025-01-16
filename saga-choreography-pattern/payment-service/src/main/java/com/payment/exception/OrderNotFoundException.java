package com.payment.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNotFound) {
    }
}
