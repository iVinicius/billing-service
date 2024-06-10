package com.ivinicius.billingservice.exceptions;

import org.springframework.http.HttpStatus;

public class BillingAuthenticationException extends BillingServiceException {

    public BillingAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

}
