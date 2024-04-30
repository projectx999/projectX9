package com.hydra.divideup.exception;

public class RecordNotFoundException extends DivideUpException {

  public RecordNotFoundException(String message) {
    super(message);
  }

  public RecordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
