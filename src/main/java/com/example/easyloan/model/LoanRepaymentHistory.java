package com.example.easyloan.model;

import com.example.easyloan.enums.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class LoanRepaymentHistory {
    @Id
    private Long id;
    private Double paymentAmount;
    private Date date;
    private PaymentMethod paymentMethod;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private LoanRequest loan;
}
