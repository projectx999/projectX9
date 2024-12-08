package com.hydra.divideup.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.service.expensemanager.EqualExpenseManager;
import com.hydra.divideup.service.expensemanager.ExpenseManagerFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  // Mocks
  @Mock private ExpenseRepository expenseRepository;
  @Mock private ExpenseManagerFactory expenseManagerFactory;
  @Mock private EqualExpenseManager equalExpenseCalculator;
  @InjectMocks private ExpenseService expenseService;

  @Test
  void createExpenseTest() {
    // Given
    List<Expense> expenseList = new ArrayList<>();
    Payment payment = new Payment();
    payment.setSplitType(SplitType.EQUAL);

    when(expenseManagerFactory.getExpenseManager(payment.getSplitType()))
        .thenReturn(equalExpenseCalculator);
    when(equalExpenseCalculator.calculateExpenses(payment)).thenReturn(expenseList);

    // when
    expenseService.createExpense(payment);

    // then
    verify(expenseManagerFactory, times(1)).getExpenseManager(payment.getSplitType());
    verify(equalExpenseCalculator, times(1)).calculateExpenses(payment);
    verify(expenseRepository, times(1)).saveAll(expenseList);
  }
}
