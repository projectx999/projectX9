package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public abstract class ExpenseCalculator {

  public List<Expense> calculateExpenses(Payment payment) {
    if (payment.getGroupId() == null || payment.getGroupId().isEmpty()) {
      return calculateExpensesForIndividualExpense(payment);
    } else {
      return calculateExpensesForGroupExpense(payment);
    }
  }

  protected abstract List<Expense> calculateExpensesForGroupExpense(Payment payment);

  protected abstract List<Expense> calculateExpensesForIndividualExpense(Payment payment);

}
