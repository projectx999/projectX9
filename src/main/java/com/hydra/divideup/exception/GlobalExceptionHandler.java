package com.hydra.divideup.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DivideUpException.class})
    public ResponseEntity<ErrorResponse> handleDivideUpException(DivideUpException divideUpException) {
        return buildErrorResponse(divideUpException.getError());
    }

    //other
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        // Define a custom response body
        String bodyOfResponse = "An error occurred";
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(DivideUpError divideUpError) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(divideUpError.getCode(), divideUpError.getMessage(), divideUpError.getTimestamp()));
    }
    public record ErrorResponse(int code, String message, LocalDateTime timestamp) {
    }

}


