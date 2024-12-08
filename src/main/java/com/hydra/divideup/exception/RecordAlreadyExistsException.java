package com.hydra.divideup.exception;

public class RecordAlreadyExistsException extends DivideUpException {

  public RecordAlreadyExistsException(DivideUpError error) {
    super(error);
  }
}
