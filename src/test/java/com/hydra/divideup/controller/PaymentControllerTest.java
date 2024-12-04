package com.hydra.divideup.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = PaymentController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class PaymentControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private PaymentService paymentService;

  @Autowired private ObjectMapper objectMapper;

  private String paymentUrl = "/api/v1/payments";

  @Test
  void testCreatePayment() throws Exception {
    // given
    final String id = "123";
    Payment payment = new Payment("paid_user", "SEK", 200, SplitType.PERCENTAGE);

    Payment createdPayment =
        new Payment(
            payment.getPaidBy(),
            payment.getCurrency(),
            payment.getAmount(),
            payment.getSplitType());
    createdPayment.setId(id);
    // when
    when(paymentService.createPayment(any(Payment.class))).thenReturn(createdPayment);

    // then
    mockMvc
        .perform(
            post(paymentUrl)
                .content(objectMapper.writeValueAsString(payment))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.currency", is(createdPayment.getCurrency().toString())))
        .andExpect(jsonPath("$.paidBy", is(createdPayment.getPaidBy())))
        .andExpect(jsonPath("$.amount", is(createdPayment.getAmount())))
        .andExpect(jsonPath("$.splitType", is(createdPayment.getSplitType().toString())));
  }
}
