package com.example.easyloan.model;


import com.example.easyloan.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_requests")
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double loanAmount;

    @Column(nullable = false)
    private Date date;
    private String reference;


    @Column(nullable = false)
    private String purpose;

    private Double interestRate;

    private String supportingDocument;

    private boolean acceptedByBorrower;
    private Double outstandingBalance;
    private Double repaymentAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private LoanOffer loanOffer;


    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
}
