package com.hydra.divideup.exception;

public class RecordAlreadyExistsException extends DivideUpException{

  public RecordAlreadyExistsException(DivideUpError error) {
    super(error);
  }

  public RecordAlreadyExistsException(DivideUpError error, Throwable cause) {
    super(error, cause);
  }
}
