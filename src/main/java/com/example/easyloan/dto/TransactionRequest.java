package com.example.easyloan.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    private Long id;
    private double transactionAmount;

}
