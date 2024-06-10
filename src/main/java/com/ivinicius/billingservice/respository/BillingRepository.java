package com.ivinicius.billingservice.respository;

import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    Page<Billing> findByDueDateAndDescriptionContaining(LocalDate dueDate, String description, Pageable pageable);
    List<Billing> findByPaymentDateBetweenAndSituation(LocalDate startDate, LocalDate endDate, BillingSituation situation);
}
