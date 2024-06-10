package com.ivinicius.billingservice.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingRequest {

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDate paymentDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255, message = "Description can have at most 255 characters")
    private String description;

    @NotNull(message = "Situation is required")
    @Pattern(regexp = "PENDING|PAID", message = "Invalid situation value. Must be: PENDING|PAID")
    private String situation;

}