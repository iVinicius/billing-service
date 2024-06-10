package com.ivinicius.billingservice.exceptions;

import org.springframework.http.HttpStatus;

public class BillingNotFoundException extends BillingServiceException {

    public BillingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
