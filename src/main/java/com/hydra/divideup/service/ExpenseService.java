package com.hydra.divideup.service;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.service.expensemanager.ExpenseManagerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  private final ExpenseManagerFactory expenseManagerFactory;

  public ExpenseService(
      ExpenseRepository expenseRepository, ExpenseManagerFactory expenseManagerFactory) {
    this.expenseRepository = expenseRepository;
    this.expenseManagerFactory = expenseManagerFactory;
  }

  public void createExpense(Payment payment) {
    expenseRepository.saveAll(
        expenseManagerFactory.getExpenseManager(payment.getSplitType()).calculateExpenses(payment));
  }
}
