package com.hydra.divideup.controller;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.service.PaymentService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping()
  public ResponseEntity<Payment> createPayment(@RequestBody @Valid Payment payment) {
    return ResponseEntity.ok(paymentService.createPayment(payment));
  }
}
