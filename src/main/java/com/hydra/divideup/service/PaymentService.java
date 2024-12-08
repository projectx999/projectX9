package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.*;
import static java.util.Objects.isNull;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.repository.PaymentRepository;
import com.hydra.divideup.service.expensemanager.ExpenseManagerFactory;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentService {

  private final ExpenseService expenseService;

  private final PaymentRepository paymentRepository;

  private final UserService userService;

  private final GroupService groupService;

  private final ExpenseManagerFactory expenseManagerFactory;

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
    if (isNull(payment.getSplitType())) {
      throw new IllegalOperationException(PAYMENT_SPLIT_TYPE);
    }

    userService.getUser(payment.getUserId());

    if (payment.getAmount() <= 0) {
      throw new IllegalOperationException(PAYMENT_AMOUNT);
    }
    validateSplitDetails(payment);
  }

  private void validateSplitDetails(Payment payment) {
    validateUsersInSplitDetails(payment.getSplitDetails());
    validateGroupUsersInSplitDetails(payment.getSplitDetails(), payment.getGroupId());
    expenseManagerFactory.getExpenseManager(payment.getSplitType()).validate(payment);
  }

  private void validateUsersInSplitDetails(Map<String, Double> splitDetails) {
    var userIds =
        splitDetails.keySet().stream().filter(Objects::nonNull).collect(Collectors.toSet());
    var users = userService.getUsers(userIds);
    if (users.size() != userIds.size()) {
      throw new IllegalOperationException(PAYMENT_SPLIT_DETAILS);
    }
  }

  private void validateGroupUsersInSplitDetails(Map<String, Double> splitDetails, String groupId) {
    var group = groupService.getGroup(groupId);
    if (!group.getMembers().containsAll(splitDetails.keySet())) {
      throw new IllegalOperationException(PAYMENT_SPLIT_DETAILS);
    }
  }
}
