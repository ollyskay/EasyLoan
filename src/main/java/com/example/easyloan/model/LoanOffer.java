package com.example.easyloan.model;

import com.example.easyloan.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class LoanOffer extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double offerAmount;

    @Column(nullable = false)
    private Integer durationInDays;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private Long borrowerId;
    private boolean successful;

    private Date dateCollected;

    @Column(nullable = false)
    private Double interestRate;

    private String offerStatus = "open";


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
