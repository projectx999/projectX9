package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_DETAILS;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.DivideUpException;
import com.hydra.divideup.exception.IllegalOperationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class ShareExpenseManager implements ExpenseManager {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());

    // Filter and count members who are not the payer
    var totalShares =
        BigDecimal.valueOf(
            payment.getSplitDetails().values().stream().mapToDouble(Double::doubleValue).sum());

    // Calculate the share price per member
    var sharePrice = amount.divide(totalShares, RoundingMode.HALF_UP);

    // Create expenses for all members except the payer
    var expenses =
        payment.getSplitDetails().entrySet().stream()
            .filter(entry -> !entry.getKey().equals(payment.getPaidBy()))
            .map(
                entry -> {
                  var shareAmount =
                      sharePrice.multiply(BigDecimal.valueOf(entry.getValue())).negate();
                  return new Expense(payment, entry.getKey(), shareAmount.stripTrailingZeros());
                })
            .collect(Collectors.toList());

    // Create expenses for the payer
    var payerAmount =
        expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).negate();
    expenses.add(new Expense(payment, payment.getPaidBy(), payerAmount.stripTrailingZeros()));
    return expenses;
  }

  @Override
  public void validate(Payment payment) throws DivideUpException {
    payment.getSplitDetails().values().stream()
        .filter(v -> v < 0)
        .findAny()
        .ifPresent(
            v -> {
              throw new IllegalOperationException(PAYMENT_SPLIT_DETAILS);
            });
  }
}
