package com.hydra.divideup.exception;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
public enum DivideUpError {

    USER_NOT_FOUND(1001,"User not found");

    private final int code;
    private final String message;
    private LocalDateTime timestamp;



    DivideUpError(int code, String message) {
        this.timestamp = LocalDateTime.now();
        this.code=code;
        this.message = message;
    }
}
