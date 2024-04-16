package com.example.easyloan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "transaction-info")
public class TransactionInfo extends BaseEntity{
        private String name;
        private String email;
        private Double amount;
        private String currency;
        private String reference;
        private String callBackUrl;
        private Date date;
        private boolean isVerified = false;
}

