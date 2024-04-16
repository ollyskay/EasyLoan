package com.example.easyloan.dto;

import com.example.easyloan.model.User;
import lombok.Data;

import java.util.Date;
@Data

public class LoanRequestDTO {



    private Double loanAmount;


    private Date date;


    private String purpose;

    private Double interestRate;

    private String supportingDocument;


    private User user;
}
