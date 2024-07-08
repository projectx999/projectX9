package com.hydra.divideup.service.calculator;

import static java.util.Objects.isNull;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public abstract sealed class ExpenseCalculator
    permits EqualExpenseCalculator,
        PercentageExpenseCalculator,
        FullExpenseCalculator,
        ShareExpenseCalculator,
        UnEqualExpenseCalculator {

  public List<Expense> calculateExpenses(Payment payment) {
    if (isNull(payment.getGroupId()) || payment.getGroupId().isEmpty()) {
      return calculateExpensesForIndividualExpense(payment);
    } else {
      return calculateExpensesForGroupExpense(payment);
    }
  }

  protected abstract List<Expense> calculateExpensesForGroupExpense(Payment payment);

  protected abstract List<Expense> calculateExpensesForIndividualExpense(Payment payment);
}
