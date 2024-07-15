package com.T82.user.exception;

public class NoEmailException extends RuntimeException {
    public NoEmailException(String message) {
        super(message);
    }
}
