package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_DETAILS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.exception.IllegalOperationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnEqualExpenseManagerTest {

  @InjectMocks private UnEqualExpenseManager unequalExpenseCalculator;

  @Test
  void testCalculateExpensesPaidBy_Involved() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(80)
            .splitDetails(
                Map.of(
                    "111",
                    Double.valueOf(10),
                    "222",
                    Double.valueOf(20),
                    "333",
                    Double.valueOf(25),
                    "444",
                    Double.valueOf(25)))
            .build();

    // when
    List<Expense> payments = unequalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(4)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-20.0)),
            tuple("333", BigDecimal.valueOf(-25.0)),
            tuple("444", BigDecimal.valueOf(-25.0)),
            tuple("111", BigDecimal.valueOf(70.0)));
  }

  @Test
  void testCalculateExpensesPaidBy_NotInvolved() {
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("100")
            .amount(80)
            .splitDetails(
                Map.of(
                    "111",
                    Double.valueOf(10),
                    "222",
                    Double.valueOf(20),
                    "333",
                    Double.valueOf(25),
                    "444",
                    Double.valueOf(25)))
            .build();

    // when
    List<Expense> payments = unequalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(5)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-20.0)),
            tuple("333", BigDecimal.valueOf(-25.0)),
            tuple("444", BigDecimal.valueOf(-25.0)),
            tuple("111", BigDecimal.valueOf(-10.0)),
            tuple("100", BigDecimal.valueOf(80.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_Involved() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("111", Double.valueOf(50), "222", Double.valueOf(30)))
            .build();

    // when
    List<Expense> payments = unequalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-30.0)), tuple("111", BigDecimal.valueOf(30.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_NotInvolved() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("222", 80.0))
            .build();

    // when
    List<Expense> payments = unequalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-80.0)), tuple("111", BigDecimal.valueOf(80.0)));
  }

  @Test
  void testValidate() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("111", 10.0, "222", 20.0, "333", 25.0, "444", 25.0))
            .build();

    // when
    unequalExpenseCalculator.validate(payment);
  }

  @Test
  void testValidateForInvalidAmount() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("111", 10.0, "222", 20.0, "333", 25.0, "444", 25.0))
            .build();

    // when
    payment.setAmount(90);
    // then
    IllegalOperationException exception =
        assertThrows(
            IllegalOperationException.class, () -> unequalExpenseCalculator.validate(payment));
    assertEquals(PAYMENT_SPLIT_DETAILS.getMessage(), exception.getMessage());
  }
}
