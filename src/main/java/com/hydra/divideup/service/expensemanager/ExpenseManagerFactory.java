package com.hydra.divideup.service.expensemanager;

import com.hydra.divideup.enums.SplitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ExpenseManagerFactory {

  private final PercentageExpenseManager percentageExpenseCalculator;
  private final UnEqualExpenseManager unEqualExpenseCalculator;
  private final ShareExpenseManager shareExpenseCalculator;
  private final EqualExpenseManager equalExpenseCalculator;

  public ExpenseManager getExpenseManager(final SplitType splitType) {
    return switch (splitType) {
      case PERCENTAGE -> percentageExpenseCalculator;
      case UNEQUAL -> unEqualExpenseCalculator;
      case EQUAL -> equalExpenseCalculator;
      case SHARE -> shareExpenseCalculator;
    };
  }
}
