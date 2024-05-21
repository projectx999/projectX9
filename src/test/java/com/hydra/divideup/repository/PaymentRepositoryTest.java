package com.hydra.divideup.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

  @Autowired
  private PaymentRepository paymentRepository;

  @BeforeEach
  public void setUp() {
    paymentRepository.deleteAll();
  }

  @Test
  void testFindByGroupIdAndIsSettledTrue() {
    // Given
    String groupId = "testGroupId";
    Payment payment = new Payment();
    payment.setGroupId(groupId);
    payment.setSettled(true);
    paymentRepository.save(payment);

    // When
    List<Payment> payments = paymentRepository.findByGroupIdAndSettledTrue(groupId);

    // Then
    assertFalse(payments.isEmpty());
    assertEquals(1, payments.size());
  }

  @Test
  void testFindByGroupIdAndIsSettledFalse() {
    // Given
    String groupId = "testGroupId";
    Payment payment = new Payment();
    payment.setGroupId(groupId);
    payment.setSettled(false);
    paymentRepository.save(payment);

    // When
    List<Payment> payments = paymentRepository.findByGroupIdAndSettledTrue(groupId);

    // Then
    assertTrue(payments.isEmpty());
  }
}
