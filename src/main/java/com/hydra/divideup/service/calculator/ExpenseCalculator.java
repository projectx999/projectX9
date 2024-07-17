package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public sealed interface ExpenseCalculator
    permits EqualExpenseCalculator,
        PercentageExpenseCalculator,
        FullExpenseCalculator,
        ShareExpenseCalculator,
        UnEqualExpenseCalculator {

  List<Expense> calculateExpenses(Payment payment);

  default BigDecimal getPayeeAmount(List<Expense> expenses) {
    return expenses.stream()
        .map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .negate();
  }
}
