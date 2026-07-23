package com.login.exceptions;

public class CredencialesInvalidasException extends LoginException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }

    public CredencialesInvalidasException(String message, Throwable cause) {
        super(message, cause);
    }
}