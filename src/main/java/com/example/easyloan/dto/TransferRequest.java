package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferRequest {
    private String source;
    private String reason;
    private Double amount;
    private String recipient;
}
