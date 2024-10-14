package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class UnEqualExpenseCalculator implements ExpenseCalculator {

  @Override
  public List<Expense> calculateExpenses(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());
    var splitDetails = payment.getSplitDetails();

    List<Expense> expenses =
        payment.getSplitDetails().entrySet().stream()
            .filter(member -> !member.getKey().equals(payment.getPaidBy()))
            .map(
                member ->
                    new Expense(
                        payment, member.getKey(), BigDecimal.valueOf(member.getValue()).negate()))
            .collect(Collectors.toCollection(ArrayList::new));

    expenses.add(
        new Expense(
            payment,
            payment.getPaidBy(),
            amount.subtract(
                BigDecimal.valueOf(splitDetails.getOrDefault(payment.getPaidBy(), 0.0)))));
    return expenses;
  }

}
