package com.hydra.divideup.service.expensemanager;

import com.hydra.divideup.enums.SplitType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ExpenseManagerFactory {

  @Autowired
  private ApplicationContext applicationContext;

  public ExpenseManager getExpenseManager(final SplitType splitType) {
    return (ExpenseManager) applicationContext.getBean(splitType.getCalculatorName());
  }
}
