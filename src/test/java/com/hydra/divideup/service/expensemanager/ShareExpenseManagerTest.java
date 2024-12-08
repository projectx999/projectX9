package com.hydra.divideup.service.expensemanager;

import static com.hydra.divideup.exception.DivideUpError.PAYMENT_SPLIT_DETAILS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
class ShareExpenseManagerTest {

  @InjectMocks ShareExpenseManager shareExpenseCalculator;

  @Test
  void testCalculateExpensesForGroupExpensePayBy_Involved() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("111", 1.0, "222", 1.0, "333", 1.0, "444", 2.0))
            .build();

    // when
    List<Expense> payments = shareExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(4)
        .extracting(Expense::getUserId, Expense::getAmount)
        .containsExactlyInAnyOrder(
            tuple("222", BigDecimal.valueOf(-20.0).stripTrailingZeros()),
            tuple("333", BigDecimal.valueOf(-20.0).stripTrailingZeros()),
            tuple("444", BigDecimal.valueOf(-40.0).stripTrailingZeros()),
            tuple("111", BigDecimal.valueOf(80.0).stripTrailingZeros()));
  }

  @Test
  void testCalculateExpensesForGroupExpensePayBy_NotInvolved() {
    // given
    String groupId = "123";

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(groupId)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("111", 0.0, "222", 1.0, "333", 1.0, "444", 2.0))
            .build();

    // when
    List<Expense> payments = shareExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(4)
        .extracting(Expense::getUserId, Expense::getAmount)
        .containsExactlyInAnyOrder(
            tuple("222", BigDecimal.valueOf(-25.0).stripTrailingZeros()),
            tuple("333", BigDecimal.valueOf(-25.0).stripTrailingZeros()),
            tuple("444", BigDecimal.valueOf(-50.0).stripTrailingZeros()),
            tuple("111", BigDecimal.valueOf(100.0).stripTrailingZeros()));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_Involved() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("111", 1.0, "222", 1.0))
            .build();

    // when
    List<Expense> payments = shareExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-50.0).stripTrailingZeros()),
            tuple("111", BigDecimal.valueOf(50.0).stripTrailingZeros()));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_NotInvolved() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("111", 0.0, "222", 1.0))
            .build();

    // when
    List<Expense> payments = shareExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-100.0).stripTrailingZeros()),
            tuple("111", BigDecimal.valueOf(100.0).stripTrailingZeros()));
  }

  @Test
  void testCalculateExpensesForSingleExpensePaidBy_NotInvolved() {
    // given

    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("222", 1.0))
            .build();

    // when
    List<Expense> payments = shareExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-100.0).stripTrailingZeros()),
            tuple("111", BigDecimal.valueOf(100.0).stripTrailingZeros()));
  }

  @Test
  void testValidateSplitShareIsNonNegative() {
    // given
    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("222", 1.0, "333", -1.0))
            .build();

    // then
    assertThatThrownBy(() -> shareExpenseCalculator.validate(payment))
        .isInstanceOf(IllegalOperationException.class)
        .hasMessage(PAYMENT_SPLIT_DETAILS.getMessage());
  }

  @Test
  void testValidateSplitShareIsZero() {
    // given
    Payment payment =
        Payment.builder()
            .id("999")
            .groupId(null)
            .paidBy("111")
            .amount(100)
            .splitDetails(Map.of("222", 1.0, "333", 0.0))
            .build();

    // then
    shareExpenseCalculator.validate(payment);
  }
}
