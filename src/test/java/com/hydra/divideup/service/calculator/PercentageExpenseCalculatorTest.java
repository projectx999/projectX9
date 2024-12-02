package com.hydra.divideup.service.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PercentageExpenseCalculatorTest {

  private PercentageExpenseCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new PercentageExpenseCalculator();
  }

  @Test
  void testCalculateExpensesForGroupExpense() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Mockito.when(payment.getAmount()).thenReturn(100.0);
    Mockito.when(payment.getPaidBy()).thenReturn("A");

    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", 40.0);
    splitDetails.put("C", 60.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Expected expenses
    Expense expense1 = new Expense(payment, "B", BigDecimal.valueOf(-40.00000000000).setScale(11));
    Expense expense2 = new Expense(payment, "C", BigDecimal.valueOf(-60.00000000000).setScale(11));
    Expense expense3 = new Expense(payment, "A", BigDecimal.valueOf(100.00000000000).setScale(11));

    List<Expense> expectedExpenses = List.of(expense1, expense2, expense3);

    // Calculate expenses
    List<Expense> actualExpenses = calculator.calculateExpenses(payment);

    // Verify the results
    assertEquals(expectedExpenses.size(), actualExpenses.size());
    assertEquals(expectedExpenses.get(0).getUserId(), actualExpenses.get(0).getUserId());
    assertEquals(expectedExpenses.get(0).getAmount(), actualExpenses.get(0).getAmount());
    assertEquals(expectedExpenses.get(1).getUserId(), actualExpenses.get(1).getUserId());
    assertEquals(expectedExpenses.get(1).getAmount(), actualExpenses.get(1).getAmount());
    assertEquals(expectedExpenses.get(2).getUserId(), actualExpenses.get(2).getUserId());
    assertEquals(expectedExpenses.get(2).getAmount(), actualExpenses.get(2).getAmount());
  }

  @Test
  void testCalculateExpensesForGroupExpense_SingleUser() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Mockito.when(payment.getAmount()).thenReturn(100.0);
    Mockito.when(payment.getPaidBy()).thenReturn("A");

    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", 100.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Expected expenses
    Expense expense1 = new Expense(payment, "A", BigDecimal.valueOf(0).setScale(11));
    Expense expense2 = new Expense(payment, "B", BigDecimal.valueOf(-100).setScale(11));

    List<Expense> expectedExpenses = List.of(expense1, expense2);

    // Calculate expenses
    List<Expense> actualExpenses = calculator.calculateExpenses(payment);

    // Verify the results
    assertEquals(expectedExpenses.size(), actualExpenses.size());
    assertEquals(expectedExpenses.get(1).getUserId(), actualExpenses.get(0).getUserId());
    assertEquals(expectedExpenses.get(1).getAmount(), actualExpenses.get(0).getAmount());
  }
}
