package com.hydra.divideup.exception;

public class DivideUpException extends RuntimeException {
  private final DivideUpError error;

  public DivideUpException(DivideUpError error) {
    super(error.getMessage());
    this.error = error;
  }

  public DivideUpException(DivideUpError error, Throwable cause) {
    super(error.getMessage(), cause);
    this.error = error;
  }
}
