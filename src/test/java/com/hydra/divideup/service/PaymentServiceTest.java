package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_VALIDATE_PAYEE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.repository.PaymentRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @InjectMocks
  private PaymentService paymentService;

  @Mock
  private ExpenseService expenseService;

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private UserService userService;

  @Mock
  private GroupService groupService;

  @Test
  void givenInvalidGroupId_whenCreatePayment_thenThrowIllegalOperationException() {
    // given
    Payment payment = new Payment();
    payment.setGroupId(null); // Invalid group ID
    payment.setUserId("testUser");
    payment.setPaidBy("user1");
    payment.setAmount(100.0);
    payment.setSplitType(SplitType.EQUAL);
    // when & then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_VALIDATE_PAYEE.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }


}
