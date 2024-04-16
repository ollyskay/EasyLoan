package com.example.easyloan.exception;

public class RecipientNotFoundException extends RuntimeException{
    public RecipientNotFoundException(String message) {
        super(message);
    }
}
