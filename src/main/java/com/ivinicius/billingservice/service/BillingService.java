package com.ivinicius.billingservice.service;

import com.ivinicius.billingservice.api.request.BillingRequest;
import com.ivinicius.billingservice.api.response.PaginatedResponse;
import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import com.ivinicius.billingservice.exceptions.BillingNotFoundException;
import com.ivinicius.billingservice.respository.BillingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BillingService {

    private final BillingRepository repository;

    private final BillingFileService fileService;

    public Long createBilling(BillingRequest request) {
        return repository.save(Billing.builder()
                        .amount(request.getAmount())
                        .dueDate(request.getDueDate())
                        .paymentDate(request.getPaymentDate())
                        .description(request.getDescription())
                        .situation(BillingSituation.valueOf(request.getSituation()))
                        .build())
                .getId();
    }

    public Billing updateBilling(Long id, BillingRequest request) throws BillingNotFoundException {
        if(!repository.existsById(id)){
            log.error("[ERROR] Billing not found with id: {}", id);
            throw new BillingNotFoundException("Billing not found with id: " + id);
        }

        return repository.save(Billing.builder()
                        .id(id)
                        .amount(request.getAmount())
                        .dueDate(request.getDueDate())
                        .paymentDate(request.getPaymentDate())
                        .description(request.getDescription())
                        .situation(BillingSituation.valueOf(request.getSituation()))
                        .build());
    }

    public Billing updateBillingSituation(Long id, String situation) throws BillingNotFoundException {
        Optional<Billing> current = repository.findById(id);
        if(current.isEmpty()){
            log.error("[ERROR] Billing not found with id: {}", id);
            throw new BillingNotFoundException("Billing not found with id: " + id);
        }

        current.get().setSituation(BillingSituation.valueOf(situation));

        return repository.save(current.get());
    }

    public Billing getById(Long id) throws BillingNotFoundException {
        Optional<Billing> current = repository.findById(id);
        if(current.isEmpty()){
            log.error("[ERROR] Billing not found with id: {}", id);
            throw new BillingNotFoundException("Billing not found with id: " + id);
        }

        return current.get();
    }

    public PaginatedResponse<Billing> getBillings(LocalDate dueDate, String description, Pageable pageable) {

        return new PaginatedResponse<>(repository.findByDueDateAndDescriptionContaining(dueDate, description, pageable));
    }

    public BigDecimal getBillingsByPaymentDateRange(LocalDate startDate, LocalDate endDate) throws BillingNotFoundException {
        List<Billing> payments = repository.findByPaymentDateBetweenAndSituation(startDate, endDate, BillingSituation.PAID);

        if(payments.isEmpty()) {
            log.error("[ERROR] Payments not found within range: startDate: {} and endDate: {}", startDate, endDate);
            throw new BillingNotFoundException("Payments not found within provided range");
        }

        return payments.stream()
                .map(Billing::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Async
    public void importBillings(File file) {
        List<Billing> billings = fileService.processFile(file);
        repository.saveAll(billings);
        log.info("[5/5] Successfully saved Billings from CSV file");
    }

}
