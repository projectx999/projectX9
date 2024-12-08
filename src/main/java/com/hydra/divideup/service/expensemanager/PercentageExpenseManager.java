package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_PERCENTAGE;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_PERCENTAGE_NOT_VALID;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.DivideUpException;
import com.hydra.divideup.exception.IllegalOperationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class PercentageExpenseManager implements ExpenseManager {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());
    List<Expense> expenses =
        payment.getSplitDetails().entrySet().stream()
            .filter(member -> !member.getKey().equals(payment.getPaidBy()))
            .map(
                member ->
                    new Expense(
                        payment,
                        member.getKey(),
                        amount
                            .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(member.getValue()))
                            .negate()))
            .collect(Collectors.toCollection(ArrayList::new));

    var payeeAmount =
        expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).negate();
    expenses.add(new Expense(payment, payment.getPaidBy(), payeeAmount));
    return expenses;
  }

  @Override
  public void validate(Payment payment) throws DivideUpException {
    var splitDetails = payment.getSplitDetails();
    var percentageSum = splitDetails.values().stream().mapToDouble(Double::doubleValue).sum();
    for (var i : splitDetails.values()) {
      if (i < 0 || i > 100) {
        throw new IllegalOperationException(PAYMENT_SPLIT_PERCENTAGE_NOT_VALID);
      }
    }
    if (percentageSum != 100) {
      throw new IllegalOperationException(PAYMENT_SPLIT_PERCENTAGE);
    }
  }
}
