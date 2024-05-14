package com.hydra.divideup.exception;

public class IllegalOperationException extends DivideUpException {

  public IllegalOperationException(DivideUpError error) {
    super(error);
  }

  public IllegalOperationException(DivideUpError error, Throwable cause) {
    super(error, cause);
  }
}
