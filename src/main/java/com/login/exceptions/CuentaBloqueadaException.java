package com.login.exceptions;

public class CuentaBloqueadaException extends LoginException {
    public CuentaBloqueadaException(String message) {
        super(message);
    }

    public CuentaBloqueadaException(String message, Throwable cause) {
        super(message, cause);
    }
}