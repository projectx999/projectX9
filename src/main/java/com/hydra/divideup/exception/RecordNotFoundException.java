package com.hydra.divideup.exception;

public class RecordNotFoundException extends DivideUpException {

  public RecordNotFoundException(DivideUpError error) {
    super(error);
  }
}
