package com.example.easyloan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Lender extends BaseEntity {
    private String loanType;
    private String documentRequired;
    private boolean loanFee;
    private String loanDecisionDuration;
    private String loanRiskStatus;

    @OneToOne
    private User user;
}
