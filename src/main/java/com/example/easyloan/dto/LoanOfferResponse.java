package com.example.easyloan.dto;

import com.example.easyloan.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoanOfferResponse {
    private double amount;
    private String date;
    private PaymentMethod paymentMethod;
    private String description;

}
