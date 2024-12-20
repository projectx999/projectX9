package com.hydra.divideup.service.expensemanager;

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
class EqualExpenseManagerTest {

  @InjectMocks private EqualExpenseManager equalExpenseCalculator;

  @Test
  void testCalculateExpensesPaidBy_Involved() {
    // given
    String groupId = "123";

    Payment payment = new Payment();
    payment.setId("999");
    payment.setGroupId(groupId);
    payment.setPaidBy("111");
    payment.setAmount(80);
    payment.setSplitDetails(Map.of("111", 0.0, "222", 0.0, "333", 0.0, "444", 0.0));

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpenses(payment);

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
  void testCalculateExpensesPaidBy_NotInvolved() {
    String groupId = "123";

    Payment payment = new Payment();
    payment.setId("999");
    payment.setGroupId(groupId);
    payment.setPaidBy("100");
    payment.setAmount(80);
    payment.setSplitDetails(Map.of("111", 0.0, "222", 0.0, "333", 0.0, "444", 0.0));

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(5)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(
            tuple("222", BigDecimal.valueOf(-20.0)),
            tuple("333", BigDecimal.valueOf(-20.0)),
            tuple("444", BigDecimal.valueOf(-20.0)),
            tuple("111", BigDecimal.valueOf(-20.0)),
            tuple("100", BigDecimal.valueOf(80.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_Involved() {
    // given

    Payment payment = new Payment();
    payment.setId("999");
    payment.setGroupId(null);
    payment.setPaidBy("111");
    payment.setAmount(80);
    payment.setSplitDetails(Map.of("111", 0.0, "222", 0.0));

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-40.0)), tuple("111", BigDecimal.valueOf(40.0)));
  }

  @Test
  void testCalculateExpensesForIndividualExpensePaidBy_NotInvolved() {
    // given

    Payment payment = new Payment();
    payment.setId("999");
    payment.setGroupId(null);
    payment.setPaidBy("111");
    payment.setAmount(80);
    payment.setSplitDetails(Map.of("222", 80.0));

    // when
    List<Expense> payments = equalExpenseCalculator.calculateExpenses(payment);

    // then
    assertThat(payments)
        .hasSize(2)
        .extracting(Expense::getUserId, Expense::getAmount)
        .contains(tuple("222", BigDecimal.valueOf(-80.0)), tuple("111", BigDecimal.valueOf(80.0)));
  }
}
