package com.ivinicius.billingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivinicius.billingservice.api.request.BillingRequest;
import com.ivinicius.billingservice.api.response.PaginatedResponse;
import com.ivinicius.billingservice.entities.Billing;
import com.ivinicius.billingservice.entities.BillingSituation;
import com.ivinicius.billingservice.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(authorities = {"READ"})
    void createBillUnauthorized() throws Exception {
        when(billingService.createBilling(any(BillingRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/api/billing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billingRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"WRITE"})
    void createBill() throws Exception {
        when(billingService.createBilling(any(BillingRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/api/billing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    @WithMockUser(authorities = {"WRITE"})
    void updateBill() throws Exception {
        when(billingService.updateBilling(anyLong(), any(BillingRequest.class))).thenReturn(billing);

        mockMvc.perform(put("/api/billing/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(10)))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    @WithMockUser(authorities = {"WRITE"})
    void updateBillingSituation() throws Exception {
        when(billingService.updateBillingSituation(anyLong(), anyString())).thenReturn(billing);

        mockMvc.perform(put("/api/billing/1/situation")
                        .param("situation", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.situation", is("PAID")));
    }

    @Test
    @WithMockUser(authorities = {"READ"})
    void getById() throws Exception {
        when(billingService.getById(anyLong())).thenReturn(billing);

        mockMvc.perform(get("/api/billing/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    @WithMockUser(authorities = {"READ"})
    void getBillings() throws Exception {
        PaginatedResponse<Billing> paginatedResponse = new PaginatedResponse<>(new PageImpl<>(Collections.singletonList(billing)));
        when(billingService.getBillings(any(LocalDate.class), anyString(), any(Pageable.class))).thenReturn(paginatedResponse);

        mockMvc.perform(get("/api/billing")
                        .param("dueDate", LocalDate.now().plusDays(30).toString())
                        .param("description", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)));
    }

    @Test
    @WithMockUser(roles = {"READ"})
    void getBillingsByPaymentDateRange() throws Exception {
        when(billingService.getBillingsByPaymentDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/api/billing/paid")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(10)));
    }

}
