package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ShareExpenseCalculator implements ExpenseCalculator {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    return List.of();
  }
}
