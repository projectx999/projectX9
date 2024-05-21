package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FullExpenseCalculator extends ExpenseCalculator{


  @Override
  protected List<Expense> calculateExpensesForGroupExpense(Payment payment) {
    return List.of();
  }

  @Override
  protected List<Expense> calculateExpensesForIndividualExpense(Payment payment) {
    return List.of();
  }
}
