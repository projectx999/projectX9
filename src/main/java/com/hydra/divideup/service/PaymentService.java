package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_PERCENTAGE;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_SHARE;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_TYPE;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_UNEQUAL;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_VALIDATE_PAYEE;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.repository.PaymentRepository;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  private final ExpenseService expenseService;

  private final PaymentRepository paymentRepository;

  public PaymentService(ExpenseService expenseService, PaymentRepository paymentRepository) {
    this.expenseService = expenseService;
    this.paymentRepository = paymentRepository;
  }

  @Transactional
  public Payment createPayment(Payment payment) {
    validatePayment(payment);
    var paymentSaved = paymentRepository.save(payment);
    expenseService.createExpense(paymentSaved);
    return paymentSaved;
  }

  private void validatePayment(Payment payment) {
    if (isNull(payment.getGroupId()) || isNull(payment.getUserId())) {
      throw new IllegalOperationException(PAYMENT_VALIDATE_PAYEE);
    }
    Map<String, Double> splitDetails = ofNullable(payment.getSplitDetails()).orElse(emptyMap());

    // validate the split type
    splitDetails.keySet().stream()
        .filter(Objects::isNull)
        .findAny()
        .ifPresent(
            k -> {
              throw new IllegalOperationException(PAYMENT_SPLIT_TYPE);
            });
    if (payment.getSplitType() == SplitType.PERCENTAGE) {
      validatePercentageSplitType(splitDetails);
    } else if (payment.getSplitType() == SplitType.SHARE) {
      validateShareSplitType(splitDetails);
    } else if (payment.getSplitType() == SplitType.UNEQUAL) {
      validateUnequalSplitType(splitDetails, payment.getAmount());
    }
  }

  private void validatePercentageSplitType(Map<String, Double> splitDetails) {
    var percentageSum = splitDetails.values().stream().mapToDouble(Double::doubleValue).sum();
    if (percentageSum != 100) {
      throw new IllegalOperationException(PAYMENT_SPLIT_PERCENTAGE);
    }
  }

  private void validateShareSplitType(Map<String, Double> splitDetails) {
    splitDetails.values().stream()
        .filter(v -> v < 0)
        .findAny()
        .ifPresent(
            v -> {
              throw new IllegalOperationException(PAYMENT_SPLIT_SHARE);
            });
  }

  private void validateUnequalSplitType(Map<String, Double> splitDetails, double totalAmount) {
    var amountSum = splitDetails.values().stream().mapToDouble(Double::doubleValue).sum();
    if (totalAmount != amountSum) {
      throw new IllegalOperationException(PAYMENT_SPLIT_UNEQUAL);
    }
  }
}
