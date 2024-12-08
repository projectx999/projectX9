package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_DETAILS;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.DivideUpException;
import com.hydra.divideup.exception.IllegalOperationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class UnEqualExpenseManager implements ExpenseManager {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    List<Expense> expenses =
        payment.getSplitDetails().entrySet().stream()
            .filter(member -> !member.getKey().equals(payment.getPaidBy()))
            .map(
                member ->
                    new Expense(
                        payment, member.getKey(), BigDecimal.valueOf(member.getValue()).negate()))
            .collect(Collectors.toCollection(ArrayList::new));
    expenses.add(new Expense(payment, payment.getPaidBy(), getPayeeAmount(expenses)));
    return expenses;
  }

  @Override
  public void validate(Payment payment) throws DivideUpException {
    var amountSum =
        payment.getSplitDetails().values().stream().mapToDouble(Double::doubleValue).sum();
    if (payment.getAmount() != amountSum) {
      throw new IllegalOperationException(PAYMENT_SPLIT_DETAILS);
    }
  }
}
