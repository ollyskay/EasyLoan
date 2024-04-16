package com.example.easyloan.service;


import com.example.easyloan.api.paystackpaymentverify.PaymentVerificationResponse;
import com.example.easyloan.dto.BankNameResponse;
import com.example.easyloan.dto.CreateRecipientDto;
import com.example.easyloan.dto.DisbursementRequest;
import com.example.easyloan.dto.LenderLoanDisbursementDto;
import com.example.easyloan.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<?> initializeTransaction(LenderLoanDisbursementDto loanDisbursementDto) throws ResourceNotFoundException;

    ResponseEntity<PaymentVerificationResponse> verifyTransaction(String reference) throws ResourceNotFoundException;

//    ResponseEntity<CreateRecipient> createRecipient(CreateRecipientDto createRecipientDto) throws ResourceNotFoundException;

    ResponseEntity<BankNameResponse> getAllBanks();

    ResponseEntity<?> createRecipient(CreateRecipientDto createRecipientDto) throws ResourceNotFoundException;

//    ResponseEntity<?> disburseLoan(LoanDisbursementDto loanDisbursementDto);

    ResponseEntity<?> disburseLoan(DisbursementRequest disbursementRequest);
}
