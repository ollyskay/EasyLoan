package com.example.easyloan.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter

public class UserNotFoundException extends RuntimeException {
    private String message;
    private String status;

    public UserNotFoundException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }
}
