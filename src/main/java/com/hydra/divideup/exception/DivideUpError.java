package com.hydra.divideup.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum DivideUpError {

    USER_NOT_FOUND(1001,"User not found");

    private final int code;
    private final String message;

    DivideUpError(int code, String message) {
        this.code=code;
        this.message = message;
    }
}
