package com.hydra.divideup.repository;

import com.hydra.divideup.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
