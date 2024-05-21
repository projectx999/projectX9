package com.hydra.divideup.service.calculator;

import com.hydra.divideup.enums.SplitType;
import org.springframework.stereotype.Component;

@Component
public class ExpenseCalculatorFactory {

  private final PercentageExpenseCalculator percentageExpenseCalculator;
  private final UnEqualExpenseCalculator unEqualExpenseCalculator;
  private final FullExpenseCalculator fullExpenseCalculator;
  private final ShareExpenseCalculator shareExpenseCalculator;
  private final EqualExpenseCalculator equalExpenseCalculator;

  public ExpenseCalculatorFactory(PercentageExpenseCalculator percentageExpenseCalculator,
      UnEqualExpenseCalculator unEqualExpenseCalculator,
      FullExpenseCalculator fullExpenseCalculator, ShareExpenseCalculator shareExpenseCalculator,
      EqualExpenseCalculator equalExpenseCalculator) {
    this.percentageExpenseCalculator = percentageExpenseCalculator;
    this.unEqualExpenseCalculator = unEqualExpenseCalculator;
    this.fullExpenseCalculator = fullExpenseCalculator;
    this.shareExpenseCalculator = shareExpenseCalculator;
    this.equalExpenseCalculator = equalExpenseCalculator;
  }

  public ExpenseCalculator getExpenseCalculator(SplitType splitType) {
    return switch (splitType) {
      case PERCENTAGE -> percentageExpenseCalculator;
      case UNEQUAL -> unEqualExpenseCalculator;
      case FULL -> fullExpenseCalculator;
      case EQUAL -> equalExpenseCalculator;
      case SHARE -> shareExpenseCalculator;
    };
  }

}
