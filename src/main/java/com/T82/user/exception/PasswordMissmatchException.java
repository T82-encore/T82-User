package com.T82.user.exception;

public class PasswordMissmatchException extends RuntimeException {
    public PasswordMissmatchException(String message) {
        super(message);
    }
}
