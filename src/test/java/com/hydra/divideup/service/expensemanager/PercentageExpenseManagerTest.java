package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_PERCENTAGE;
import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_PERCENTAGE_NOT_VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.IllegalOperationException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PercentageExpenseManagerTest {

  private PercentageExpenseManager calculator;

  @BeforeEach
  void setUp() {
    calculator = new PercentageExpenseManager();
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

  @Test
  void testValidateExpenseForPercentageBetween1And100() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", 40.0);
    splitDetails.put("C", 60.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Validate the payment
    calculator.validate(payment);
  }

  @Test
  void testValidateExpenseForPercentageLessThan0() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", -40.0);
    splitDetails.put("C", 60.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Validate the payment
    IllegalOperationException exception =
        assertThrows(IllegalOperationException.class, () -> calculator.validate(payment));
    assertEquals(PAYMENT_SPLIT_PERCENTAGE_NOT_VALID.getMessage(), exception.getMessage());
  }

  @Test
  void testValidateExpenseForPercentageGreaterThan100() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", 40.0);
    splitDetails.put("C", 160.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Validate the payment
    IllegalOperationException exception =
        assertThrows(IllegalOperationException.class, () -> calculator.validate(payment));
    assertEquals(PAYMENT_SPLIT_PERCENTAGE_NOT_VALID.getMessage(), exception.getMessage());
  }

  @Test
  void testValidateExpenseForPercentageSumNotEqualTo100() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 0.0);
    splitDetails.put("B", 40.0);
    splitDetails.put("C", 50.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Validate the payment
    IllegalOperationException exception =
        assertThrows(IllegalOperationException.class, () -> calculator.validate(payment));
    assertEquals(PAYMENT_SPLIT_PERCENTAGE.getMessage(), exception.getMessage());
  }

  @Test
  void testValidateExpenseForPercentageSumEqualTo100() {
    // Setup payment data
    Payment payment = Mockito.mock(Payment.class);
    Map<String, Double> splitDetails = new HashMap<>();
    splitDetails.put("A", 20.0);
    splitDetails.put("B", 40.0);
    splitDetails.put("C", 40.0);
    Mockito.when(payment.getSplitDetails()).thenReturn(splitDetails);

    // Validate the payment
    calculator.validate(payment);
  }
}
