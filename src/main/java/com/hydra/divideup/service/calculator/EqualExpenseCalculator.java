package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.repository.GroupRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class EqualExpenseCalculator extends ExpenseCalculator {

  private final GroupRepository groupRepository;

  @Override
  protected List<Expense> calculateExpensesForGroupExpense(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());
    BigDecimal splitAmount =
        amount.divide(new BigDecimal(payment.getSplitDetails().size()), RoundingMode.HALF_UP);
    List<Expense> expenses =
        payment.getSplitDetails().keySet().stream()
            .filter(member -> !member.equals(payment.getPaidBy()))
            .map(member -> new Expense(payment, member, splitAmount.negate()))
            .collect(Collectors.toCollection(ArrayList::new));
    expenses.add(new Expense(payment, payment.getPaidBy(), amount.subtract(splitAmount)));
    return expenses;
  }

  @Override
  protected List<Expense> calculateExpensesForIndividualExpense(Payment payment) {
    var amount = BigDecimal.valueOf(payment.getAmount());
    BigDecimal splitAmount = amount.divide(new BigDecimal("2"), RoundingMode.HALF_UP);
    return List.of(
        new Expense(payment, payment.getUserId(), splitAmount.negate()),
        new Expense(payment, payment.getPaidBy(), amount.subtract(splitAmount)));
  }
}
