package com.T82.user.global;

import com.T82.user.exception.DuplicateNumberException;
import com.T82.user.exception.PasswordMissmatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionController {

//    Valid 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

//    Password 일치하지 않을때 예외 처리
    @ExceptionHandler(PasswordMissmatchException.class)
    public ResponseEntity<Map<String, String>> passwordException(PasswordMissmatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

//    핸드폰 번호 중복될때 예외처리
    @ExceptionHandler(DuplicateNumberException.class)
    public ResponseEntity<Map<String, String>> duplicateNumberException(DuplicateNumberException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }
}
