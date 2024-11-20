package com.hydra.divideup.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public enum DivideUpError {
  USER_NOT_FOUND(1001, "User not found"),
  USER_ALREADY_EXISTS(1002, "User with emailId or phoneNumber already exists"),
  USER_EMAIL_EXISTS(1003, "User with emailId already exists"),
  USER_PHONE_EXISTS(1004, "User with phoneNumber already exists"),
  GROUP_NOT_FOUND(2001, "Group not found"),
  GROUP_DELETE_UNSETTLE(2002, "Group is not settled for delete"),
  PAYMENT_VALIDATE_PAYEE(3001, "Valid group or user are required"),
  PAYMENT_SPLIT_TYPE(3002, "Invalid split type, unknown or null split type present"),
  PAYMENT_SPLIT_DETAILS(3003, "Invalid split details, invalid user details present in split details"),
  PAYMENT_SPLIT_PERCENTAGE(3004, "Sum of split details should be 100 for percentage split type"),
  PAYMENT_SPLIT_SHARE(3005, "Share value should be positive"),
  PAYMENT_SPLIT_UNEQUAL(3006, "Sum of split details should be equal to amount"),
  PAYMENT_AMOUNT(3007, "Amount should be positive"),
  PAYMENT_VALIDATE_PAID_BY(3008, "Invalid paid by user");
  private final int code;
  private final String message;
  private final LocalDateTime timestamp;

  DivideUpError(int code, String message) {
    this.timestamp = LocalDateTime.now();
    this.code = code;
    this.message = message;
  }
}
