package com.hydra.divideup.service;

import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  public ExpenseService(ExpenseRepository expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  public void createExpense(Payment payment) {

  }
}
