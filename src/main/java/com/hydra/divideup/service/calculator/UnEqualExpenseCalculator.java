package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class UnEqualExpenseCalculator extends ExpenseCalculator {

  @Override
  protected List<Expense> calculateExpensesForGroupExpense(Payment payment) {
    return List.of();
  }

  @Override
  protected List<Expense> calculateExpensesForIndividualExpense(Payment payment) {
    return List.of();
  }
}
