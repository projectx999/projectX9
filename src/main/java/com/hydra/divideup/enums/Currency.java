package com.hydra.divideup.enums;

import lombok.Getter;

@Getter
public enum Currency {
  // Alphabetical order
  INR("Indian Rupee"),
  SEK("Swedish Krona"),
  USD("US Dollar");

  private String name;

  Currency(String name) {
    this.name = name;
  }
}
