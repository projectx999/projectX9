package com.hydra.divideup.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DivideUpException.class})
    public ResponseEntity<ErrorResponse> handleDivideUpException(DivideUpException divideUpException) {
        return buildErrorResponse(divideUpException.getError());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(DivideUpError divideUpError) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(divideUpError.getCode(), divideUpError.getMessage(), divideUpError.getTimestamp()));
    }
    public record ErrorResponse(int code, String message, LocalDateTime timestamp) {
    }

}