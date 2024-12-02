package com.hydra.divideup.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Autowired private PaymentRepository paymentRepository;

  @BeforeEach
  public void setUp() {
    paymentRepository.deleteAll();
  }

  @Test
  void testFindByGroupIdAndIsSettledTrue() {
    // Given
    String groupId = "testGroupId";
    Payment payment = Payment.builder().groupId(groupId).settled(true).build();
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
    Payment payment = Payment.builder().groupId(groupId).settled(false).build();
    paymentRepository.save(payment);

    // When
    List<Payment> payments = paymentRepository.findByGroupIdAndSettledTrue(groupId);

    // Then
    assertTrue(payments.isEmpty());
  }
}
