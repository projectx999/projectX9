package com.hydra.divideup.service.expensemanager;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.DivideUpException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public sealed interface ExpenseManager
    permits EqualExpenseManager,
        PercentageExpenseManager,
        ShareExpenseManager,
        UnEqualExpenseManager {

  List<Expense> calculateExpenses(Payment payment);

  void validate(Payment payment) throws DivideUpException;

  default BigDecimal getPayeeAmount(List<Expense> expenses) {
    return expenses.stream()
        .map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .negate();
  }
}
