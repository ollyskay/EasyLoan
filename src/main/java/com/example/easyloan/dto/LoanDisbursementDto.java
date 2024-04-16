package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanDisbursementDto {
    private String borrowerName;
    private String borrowerUsername;
    private Double loanAmount;
    private String loanPurpose;
    private String callBackUrl;
    private String bankName;
}
