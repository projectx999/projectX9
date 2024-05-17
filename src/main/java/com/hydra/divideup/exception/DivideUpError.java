package com.hydra.divideup.exception;

import java.time.LocalDateTime;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public enum DivideUpError {

  USER_NOT_FOUND(1001, "User not found"),
  USER_ALREADY_EXISTS(1002, "User with emailId or phoneNumber already exists"),
  GROUP_NOT_FOUND(2001, "Group not found"),
  GROUP_DELETE_UNSETTLE(2002, "Group is not settled for delete");

  private final int code;
  private final String message;
  private final LocalDateTime timestamp;

  DivideUpError(int code, String message) {
    this.timestamp = LocalDateTime.now();
    this.code=code;
    this.message = message;
  }
}
