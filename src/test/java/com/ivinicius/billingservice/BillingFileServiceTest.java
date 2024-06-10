package com.ivinicius.billingservice;

import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import com.ivinicius.billingservice.service.BillingFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BillingFileServiceTest {

    private BillingFileService billingFileService;

    @BeforeEach
    void setUp() {
        billingFileService = new BillingFileService();
    }

    @Test
    void testProcessFile() throws IOException {
        Path tempFile = Files.createTempFile("test-billing", ".csv");
        try (Writer writer = new FileWriter(tempFile.toFile())) {
            writer.write("paymentDate,amount,situation,dueDate,description\n");
            writer.write("2023-06-08,100.00,PAID,2023-07-08,Test Description 1\n");
            writer.write("2023-06-09,200.00,PENDING,2023-07-09,Test Description 2\n");
        }

        List<Billing> billings = billingFileService.processFile(tempFile.toFile());

        assertEquals(2, billings.size());

        Billing billing1 = billings.get(0);
        assertEquals(LocalDate.of(2023, 6, 8), billing1.getPaymentDate());
        assertEquals(new BigDecimal("100.00"), billing1.getAmount());
        assertEquals(BillingSituation.PAID, billing1.getSituation());
        assertEquals(LocalDate.of(2023, 7, 8), billing1.getDueDate());
        assertEquals("Test Description 1", billing1.getDescription());

        Billing billing2 = billings.get(1);
        assertEquals(LocalDate.of(2023, 6, 9), billing2.getPaymentDate());
        assertEquals(new BigDecimal("200.00"), billing2.getAmount());
        assertEquals(BillingSituation.PENDING, billing2.getSituation());
        assertEquals(LocalDate.of(2023, 7, 9), billing2.getDueDate());
        assertEquals("Test Description 2", billing2.getDescription());
    }

    @Test
    void testProcessFileWithIOException() {
        File file = new File("non-existent-file.csv");

        List<Billing> billings = billingFileService.processFile(file);

        assertTrue(billings.isEmpty());
    }
}
