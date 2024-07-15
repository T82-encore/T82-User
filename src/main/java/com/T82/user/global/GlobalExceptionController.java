package com.T82.user.global;

import com.T82.user.domain.dto.response.ErrorResponse;
import com.T82.user.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionController {

//    Valid 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(new ErrorResponse(error.getField(), error.getDefaultMessage())));
        return ResponseEntity.badRequest().body(errors);
    }

    // Password 일치하지 않을때 예외 처리
    @ExceptionHandler(PasswordMissmatchException.class)
    public ResponseEntity<ErrorResponse> passwordException(PasswordMissmatchException ex) {
        ErrorResponse error = new ErrorResponse("password", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // 핸드폰 번호 중복될때 예외처리
    @ExceptionHandler(DuplicateNumberException.class)
    public ResponseEntity<ErrorResponse> duplicateNumberException(DuplicateNumberException ex) {
        ErrorResponse error = new ErrorResponse("phoneNumber", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // 이메일 중복될때 예외처리
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> duplicateEmailException(DuplicateEmailException ex) {
        ErrorResponse error = new ErrorResponse("email", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

//    유저 없을때 예외처리
    @ExceptionHandler(NoUserException.class)
    public ResponseEntity<ErrorResponse> noUserException(NoUserException ex) {
        ErrorResponse error = new ErrorResponse("user", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    //    이메일 없을때 예외처리
    @ExceptionHandler(NoEmailException.class)
    public ResponseEntity<ErrorResponse> noEmailException(NoEmailException ex) {
        ErrorResponse error = new ErrorResponse("email", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

}
