package com.example.easyloan.dto;

import com.example.easyloan.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoanOfferDTO {
    private Double offerAmount;
    private Integer durationInDays;
    private String description;
    private String date;
    private PaymentMethod paymentMethod;
    private Long borrowerId;
    private boolean successful;
    private Date dateCollected;
    private Double interestRate;
}

