package com.hydra.divideup.exception;

public class DivideUpException extends RuntimeException {

  public DivideUpException(String message) {
    super(message);
  }

  public DivideUpException(String message, Throwable cause) {
    super(message, cause);
  }

}
