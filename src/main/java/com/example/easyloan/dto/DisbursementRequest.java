package com.example.easyloan.dto;

import lombok.Data;

@Data
public class DisbursementRequest {
    private LoanDisbursementDto loanDisbursementDto;
    private CreateRecipientDto createRecipientDto;
}
