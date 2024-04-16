package com.example.easyloan.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T>{

    private String code;
    private String message;
    private T data;
    private String status;

    @JsonIgnore
    private HttpStatus httpStatus;

    public ApiResponse(String code, String message, T data, String status) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.status = status;
    }


    public ApiResponse(String code, String message, T data, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.httpStatus = httpStatus;

    }


    public ApiResponse(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    public ApiResponse(Map<String, String> map) {
        this.status = map.get("status");
        this.message = map.get("message");
    }


    public ApiResponse(String message) {
        this.message = message;
    }

}

