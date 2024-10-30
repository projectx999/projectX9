package com.hydra.divideup.service.calculator;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class PercentageExpenseCalculator implements ExpenseCalculator {

    @Override
    public List<Expense> calculateExpenses(Payment payment) {
        var amount = BigDecimal.valueOf(payment.getAmount());
        List<Expense> expenses =
                payment.getSplitDetails().entrySet().stream()
                        .filter(member -> !member.getKey().equals(payment.getPaidBy()))
                        .map(member -> new Expense(payment, member.getKey(), amount.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(member.getValue())).negate()))
                        .collect(Collectors.toCollection(ArrayList::new));
        expenses.add(new Expense(payment, payment.getPaidBy(), getPayeeAmount(expenses)));
        return expenses;
    }
}
