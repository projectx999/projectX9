package com.hydra.divideup.service.calculator;

import com.hydra.divideup.enums.SplitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ExpenseCalculatorFactory {

  private final PercentageExpenseCalculator percentageExpenseCalculator;
  private final UnEqualExpenseCalculator unEqualExpenseCalculator;
  private final FullExpenseCalculator fullExpenseCalculator;
  private final ShareExpenseCalculator shareExpenseCalculator;
  private final EqualExpenseCalculator equalExpenseCalculator;

  public ExpenseCalculator getExpenseCalculator(final SplitType splitType) {
    return switch (splitType) {
      case PERCENTAGE -> percentageExpenseCalculator;
      case UNEQUAL -> unEqualExpenseCalculator;
      case FULL -> fullExpenseCalculator;
      case EQUAL -> equalExpenseCalculator;
      case SHARE -> shareExpenseCalculator;
    };
  }
}
