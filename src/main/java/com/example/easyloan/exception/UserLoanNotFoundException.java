package com.example.easyloan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserLoanNotFoundException extends RuntimeException {

    public UserLoanNotFoundException(String message) {
        super(message);
    }
}

