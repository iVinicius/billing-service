package com.ivinicius.billingservice.api;

import com.ivinicius.billingservice.api.request.BillingRequest;
import com.ivinicius.billingservice.api.response.PaginatedResponse;
import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.exceptions.BillingNotFoundException;
import com.ivinicius.billingservice.service.BillingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/billing")
@Validated
public class BillingController {

    private final BillingService service;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<Long> createBill(@Valid @RequestBody BillingRequest request){

        Long billing = service.createBilling(request);

        return ResponseEntity.status(201).body(billing);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<Billing> updateBill(@PathVariable Long id,
                                              @Valid @RequestBody BillingRequest request) throws BillingNotFoundException {

        Billing billing = service.updateBilling(id, request);

        return ResponseEntity.status(200).body(billing);
    }

    @PutMapping("/{id}/situation")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<Billing> updateBillingSituation(@PathVariable Long id,
                                                          @RequestParam @Valid @Pattern(regexp = "PENDING|PAID", message = "Invalid situation value. Must be: PENDING|PAID") String situation) throws BillingNotFoundException {

        Billing billing = service.updateBillingSituation(id, situation);

        return ResponseEntity.status(200).body(billing);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billing> getById(@PathVariable Long id) throws BillingNotFoundException {

        Billing billing = service.getById(id);

        return ResponseEntity.status(200).body(billing);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<Billing>> getBillings(
            @RequestParam LocalDate dueDate,
            @RequestParam String description,
            Pageable pageable) {

        PaginatedResponse<Billing> billings = service.getBillings(dueDate, description, pageable);
        return new ResponseEntity<>(billings, HttpStatus.OK);
    }

    @GetMapping("/paid")
    public ResponseEntity<BigDecimal> getBillingsByPaymentDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) throws BillingNotFoundException {

        BigDecimal paidInRange = service.getBillingsByPaymentDateRange(startDate, endDate);
        return new ResponseEntity<>(paidInRange, HttpStatus.OK);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<String> importBillings(
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("[1/5] Starting file upload");

        Path tempDir = Files.createTempDirectory("uploads");
        Path tempFile = tempDir.resolve(file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        log.info("[2/5] Finished file upload");

        // Trigger asynchronous processing
        service.importBillings(tempFile.toFile());

        return ResponseEntity.status(HttpStatus.OK).body("Upload success. Processing is async.");
    }
}
