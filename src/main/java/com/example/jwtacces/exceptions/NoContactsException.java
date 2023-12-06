package com.example.jwtacces.exceptions;

public class NoContactsException extends RuntimeException {

    public NoContactsException(String message) {
        super(message);
    }
    public NoContactsException(String message, Throwable cause) {
        super(message, cause);
    }
}
