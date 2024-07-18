package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class EqualExpenseCalculator implements ExpenseCalculator {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());
    BigDecimal splitAmount =
        amount.divide(new BigDecimal(payment.getSplitDetails().size()), RoundingMode.HALF_UP);
    List<Expense> expenses =
        payment.getSplitDetails().keySet().stream()
            .filter(member -> !member.equals(payment.getPaidBy()))
            .map(member -> new Expense(payment, member, splitAmount.negate()))
            .collect(Collectors.toCollection(ArrayList::new));
    expenses.add(new Expense(payment, payment.getPaidBy(), getPayeeAmount(expenses)));
    return expenses;
  }
}