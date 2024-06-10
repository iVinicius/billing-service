package com.ivinicius.billingservice;

import com.ivinicius.billingservice.api.request.BillingRequest;
import com.ivinicius.billingservice.api.response.PaginatedResponse;
import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import com.ivinicius.billingservice.exceptions.BillingNotFoundException;
import com.ivinicius.billingservice.respository.BillingRepository;
import com.ivinicius.billingservice.service.BillingFileService;
import com.ivinicius.billingservice.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository repository;

    @Mock
    private BillingFileService fileService;

    @InjectMocks
    private BillingService billingService;

    private BillingRequest billingRequest;
    private Billing billing;

    @BeforeEach
    void setUp() {
        billingRequest = new BillingRequest();
        billingRequest.setAmount(BigDecimal.TEN);
        billingRequest.setDueDate(LocalDate.now().plusDays(30));
        billingRequest.setPaymentDate(LocalDate.now());
        billingRequest.setDescription("Test Description");
        billingRequest.setSituation("PAID");

        billing = Billing.builder()
                .id(1L)
                .amount(BigDecimal.TEN)
                .dueDate(LocalDate.now().plusDays(30))
                .paymentDate(LocalDate.now())
                .description("Test Description")
                .situation(BillingSituation.PAID)
                .build();
    }

    @Test
    void createBilling() {
        when(repository.save(any(Billing.class))).thenReturn(billing);

        Long id = billingService.createBilling(billingRequest);

        assertNotNull(id);
        assertEquals(1L, id);
    }

    @Test
    void updateBilling() throws BillingNotFoundException {
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.save(any(Billing.class))).thenReturn(billing);

        Billing updatedBilling = billingService.updateBilling(1L, billingRequest);

        assertNotNull(updatedBilling);
        assertEquals(1L, updatedBilling.getId());
    }

    @Test
    void updateBilling_NotFound() {
        when(repository.existsById(anyLong())).thenReturn(false);

        BillingNotFoundException exception = assertThrows(BillingNotFoundException.class, () -> {
            billingService.updateBilling(1L, billingRequest);
        });

        assertEquals("Billing not found with id: 1", exception.getMessage());
    }

    @Test
    void updateBillingSituation() throws BillingNotFoundException {
        when(repository.findById(anyLong())).thenReturn(Optional.of(billing));
        when(repository.save(any(Billing.class))).thenReturn(billing);

        Billing updatedBilling = billingService.updateBillingSituation(1L, "PAID");

        assertNotNull(updatedBilling);
        assertEquals(BillingSituation.PAID, updatedBilling.getSituation());
    }

    @Test
    void updateBillingSituation_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        BillingNotFoundException exception = assertThrows(BillingNotFoundException.class, () -> {
            billingService.updateBillingSituation(1L, "PAID");
        });

        assertEquals("Billing not found with id: 1", exception.getMessage());
    }

    @Test
    void getById() throws BillingNotFoundException {
        when(repository.findById(anyLong())).thenReturn(Optional.of(billing));

        Billing foundBilling = billingService.getById(1L);

        assertNotNull(foundBilling);
        assertEquals(1L, foundBilling.getId());
    }

    @Test
    void getById_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        BillingNotFoundException exception = assertThrows(BillingNotFoundException.class, () -> {
            billingService.getById(1L);
        });

        assertEquals("Billing not found with id: 1", exception.getMessage());
    }

    @Test
    void getBillings() {
        Page<Billing> page = new PageImpl<>(Collections.singletonList(billing));
        when(repository.findByDueDateAndDescriptionContaining(any(LocalDate.class), anyString(), any(Pageable.class))).thenReturn(page);

        PaginatedResponse<Billing> response = billingService.getBillings(LocalDate.now().plusDays(30), "Test", Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

    @Test
    void getBillingsByPaymentDateRange() throws BillingNotFoundException {
        List<Billing> payments = List.of(billing);
        when(repository.findByPaymentDateBetweenAndSituation(any(LocalDate.class), any(LocalDate.class), any(BillingSituation.class))).thenReturn(payments);

        BigDecimal totalAmount = billingService.getBillingsByPaymentDateRange(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertEquals(BigDecimal.TEN, totalAmount);
    }

    @Test
    void getBillingsByPaymentDateRange_NotFound() {
        when(repository.findByPaymentDateBetweenAndSituation(any(LocalDate.class), any(LocalDate.class), any(BillingSituation.class))).thenReturn(Collections.emptyList());

        BillingNotFoundException exception = assertThrows(BillingNotFoundException.class, () -> {
            billingService.getBillingsByPaymentDateRange(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        });

        assertEquals("Payments not found within provided range", exception.getMessage());
    }

    @Test
    void importBillings() {
        File file = mock(File.class);
        List<Billing> billings = List.of(billing);
        when(fileService.processFile(any(File.class))).thenReturn(billings);

        billingService.importBillings(file);

        verify(repository, times(1)).saveAll(billings);
    }
}

