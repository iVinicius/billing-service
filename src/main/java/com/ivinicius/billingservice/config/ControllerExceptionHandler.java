package com.ivinicius.billingservice.config;

import com.ivinicius.billingservice.exceptions.BillingServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute("exceptionHandled", true);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "Validation error");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        body.put("message", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute("exceptionHandled", true);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorBody(ex, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(BillingServiceException.class)
    protected ResponseEntity<Object> handleBillingServiceException(BillingServiceException ex, HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute("exceptionHandled", true);
        return ResponseEntity.status(ex.getStatus()).body(buildErrorBody(ex, ex.getStatus()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute("exceptionHandled", true);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorBody(ex, HttpStatus.BAD_REQUEST));
    }

    private Map<String, Object> buildErrorBody(Exception ex, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", "Operation failed");
        body.put("message", ex.getLocalizedMessage());

        return body;
    }
}

