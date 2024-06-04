package com.hydra.divideup.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public enum DivideUpError {

  USER_NOT_FOUND(1001, "User not found"),
  USER_PHONE_EXISTS(1002, "User with phoneNumber already exists"),
  USER_EMAIL_EXISTS(1003, "User with emailId already exists");

  private final int code;
  private final String message;
  private LocalDateTime timestamp;


  DivideUpError(int code, String message) {
    this.timestamp = LocalDateTime.now();
    this.code = code;
    this.message = message;
  }
}
