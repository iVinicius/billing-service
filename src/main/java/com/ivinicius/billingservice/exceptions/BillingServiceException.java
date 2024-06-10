package com.ivinicius.billingservice.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BillingServiceException extends Exception {
    private final HttpStatus status;

    public BillingServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
