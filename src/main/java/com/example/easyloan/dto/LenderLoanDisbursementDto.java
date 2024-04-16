package com.example.easyloan.dto;

import com.example.easyloan.enums.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
    public class LenderLoanDisbursementDto {
        private String name;
        private String username;
        private PaymentMethod paymentMethod;
        private List<LoanDisbursementDto> disbursementPayments;
}
