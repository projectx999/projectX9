package com.hydra.divideup.enums;

import lombok.Getter;

@Getter
public enum SplitType {
  EQUAL("equalExpenseCalculator"),
  UNEQUAL("unEqualExpenseCalculator"),
  PERCENTAGE("percentageExpenseCalculator"),
  SHARE("shareExpenseCalculator"),
  FULL("fullExpenseCalculator");

  private final String calculatorName;

  SplitType(String calculatorName) {
    this.calculatorName = calculatorName;
  }

}
