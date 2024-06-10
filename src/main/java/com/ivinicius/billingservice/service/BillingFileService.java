package com.ivinicius.billingservice.service;

import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class BillingFileService {

    public List<Billing> processFile(File file) {
        log.info("[3/5] Processing CSV file");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            List<Billing> billings = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                Billing billing = Billing.builder()
                        .paymentDate(LocalDate.parse(csvRecord.get("paymentDate")))
                        .amount(new BigDecimal(csvRecord.get("amount")))
                        .situation(BillingSituation.valueOf(csvRecord.get("situation")))
                        .dueDate(LocalDate.parse(csvRecord.get("dueDate")))
                        .description(csvRecord.get("description")).build();

                billings.add(billing);
            }
            log.info("[4/5] Successfully processed CSV file");
            return billings;
        } catch (Exception e) {
            log.error("[-4/5] Failed to process CSV file. Exception: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
