package com.ecom.salezone.exceptions;

public class BadApiRequestException extends RuntimeException{
    public BadApiRequestException() {
        super("Bad Request !!");
    }
    public BadApiRequestException(String message) {
        super(message);
    }
}
