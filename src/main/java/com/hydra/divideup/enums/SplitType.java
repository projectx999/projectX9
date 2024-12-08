package com.hydra.divideup.enums;

import lombok.Getter;

@Getter
public enum SplitType {
  EQUAL("equalExpenseManager"),
  UNEQUAL("unEqualExpenseManager"),
  PERCENTAGE("percentageExpenseManager"),
  SHARE("shareExpenseManager");

  private final String calculatorName;

  SplitType(String calculatorName) {
    this.calculatorName = calculatorName;
  }

}
