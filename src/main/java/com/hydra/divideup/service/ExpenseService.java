package com.hydra.divideup.service;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.service.calculator.ExpenseCalculatorFactory;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  private final ExpenseCalculatorFactory expenseCalculatorFactory;

  public ExpenseService(
      ExpenseRepository expenseRepository, ExpenseCalculatorFactory expenseCalculatorFactory) {
    this.expenseRepository = expenseRepository;
    this.expenseCalculatorFactory = expenseCalculatorFactory;
  }

  public void createExpense(Payment payment) {
    expenseRepository.saveAll(
        expenseCalculatorFactory
            .getExpenseCalculator(payment.getSplitType())
            .calculateExpenses(payment));
  }
}
