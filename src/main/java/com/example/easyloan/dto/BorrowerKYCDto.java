package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowerKYCDto {

    private String phoneNumber;

    private String employedBefore;
    private String currentEmploymentStatus;
    private String earning;
    private String workType;


    private String employmentStatus;
    private String otherIncomeSource;
    private Integer monthlyPersonalIncome;
    private String extraIncomeDescription;


    private String docType;
    private Long docNumber;
    private String fileNameGov;

    private String fileNameAdd;

    private String bank;
    private Long accountNumber;
    private String accountName;

    private int progress;
}
