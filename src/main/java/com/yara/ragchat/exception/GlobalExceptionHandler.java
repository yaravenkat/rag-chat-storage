package com.yara.ragchat.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest req) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError f : ex.getBindingResult().getFieldErrors()) {
            errors.put(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now(),
                "errors", errors
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", OffsetDateTime.now(),
                "error", "Type mismatch: " + ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest req) {
        // Log exception in real app
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", OffsetDateTime.now(),
                "error", "Internal Server Error"
        ));
    }
}
