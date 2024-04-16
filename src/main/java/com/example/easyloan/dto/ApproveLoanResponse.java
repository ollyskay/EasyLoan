package com.example.easyloan.dto;

import lombok.Data;

@Data
public class ApproveLoanResponse {
    private String message;
    private int statusCode;
    private String fullName;

}
