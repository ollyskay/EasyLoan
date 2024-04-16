package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LendingOfferDto {
    private Long id;
    private Double offerAmount;
    private Integer durationInDays;
    private String description;
    private Date dateCollected;
    private Double interestRate;
    private Boolean available;
    private Long borrowerId;
    private Date timeCreated;
}
