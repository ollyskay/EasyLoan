package com.example.easyloan.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LenderKYCDto {
    private String phoneNumber;

    private String loanType;
    private String documentRequired;
    private boolean loanFee;
    private String loanDecisionDuration;



    private String docType;
    private Long docNumber;
    private String fileNameGovId;

    private String loanRiskStatus;

    private String bank;
    private Long accountNumber;
    private String accountName;

    private String fileNameAdd;


    private int progress;
}
