package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDto {

    private String transferCode;
    private String status;
    private String reference;
    private double amount;
    private String recipientName;
    private String bankName;
    private String accountNumber;
}
