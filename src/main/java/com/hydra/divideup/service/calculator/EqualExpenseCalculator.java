package com.hydra.divideup.service.calculator;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.GroupRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EqualExpenseCalculator extends ExpenseCalculator {

  private final GroupRepository groupRepository;

  public EqualExpenseCalculator(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Override
  protected List<Expense> calculateExpensesForGroupExpense(Payment payment) {
    var group = groupRepository.findById(payment.getGroupId())
        .orElseThrow(() -> new RecordNotFoundException(GROUP_NOT_FOUND));
    var amount = BigDecimal.valueOf(payment.getAmount());
    BigDecimal splitAmount = amount.divide(new BigDecimal(group.getMembers().size()),
        RoundingMode.HALF_UP);
    List<Expense> expenses = group.getMembers().stream()
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
    return List.of(new Expense(payment, payment.getUserId(), splitAmount.negate()),
        new Expense(payment, payment.getPaidBy(), amount.subtract(splitAmount)));
  }
}
