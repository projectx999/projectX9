package com.hydra.divideup.service.calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EqualExpenseCalculatorTest {

  @InjectMocks private EqualExpenseCalculator equalExpenseCalculator;

  @Test
  void testCalculateExpensesForGroupExpensePayByIn() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("111", 0.0, "222", 0.0, "333", 0.0, "444", 0.0))
            .build();

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpensesForGroupExpense(payment);

    // then
    assertThat(payments)
        .hasSize(4)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-20.0)),
            tuple("333", BigDecimal.valueOf(-20.0)),
            tuple("444", BigDecimal.valueOf(-20.0)),
            tuple("111", BigDecimal.valueOf(60.0)));
  }

  @Test
  void testCalculateExpensesForGroupExpensePayByOut() {
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("100")
            .amount(80)
            .splitDetails(Map.of("111", 0.0, "222", 0.0, "333", 0.0, "444", 0.0))
            .build();

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpensesForGroupExpense(payment);

    // then
    assertThat(payments)
        .hasSize(5)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-20.0)),
            tuple("333", BigDecimal.valueOf(-20.0)),
            tuple("444", BigDecimal.valueOf(-20.0)),
            tuple("111", BigDecimal.valueOf(-20.0)))
        .doesNotContain(tuple("100", BigDecimal.valueOf(80.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidByIn() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(80)
            .splitDetails(Map.of("111", 0.0, "222", 0.0))
            .build();

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpensesForGroupExpense(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-40.0)), tuple("111", BigDecimal.valueOf(40.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidByOut() {
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
    List<Expense> payments = equalExpenseCalculator.calculateExpensesForGroupExpense(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-80.0)), tuple("111", BigDecimal.valueOf(80.0)));
  }

}
