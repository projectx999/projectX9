package com.hydra.divideup.service.calculator;

import com.hydra.divideup.enums.SplitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public final class ExpenseCalculatorFactory {

  @Autowired
  private ApplicationContext applicationContext;

  public ExpenseCalculator getExpenseCalculator(final SplitType splitType) {
    return (ExpenseCalculator) applicationContext.getBean(splitType.getCalculatorName());
  }
}
