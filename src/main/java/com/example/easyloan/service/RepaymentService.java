package com.example.easyloan.service;

import com.example.easyloan.dto.CreateRecipientDto;
import com.example.easyloan.dto.InitiateTransferDto;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.model.BankAccount;

import org.springframework.http.ResponseEntity;

public interface RepaymentService {
//    ResponseEntity<?> initializeTransaction(BorrowerLoanRepaymentDto borrowerLoanRepaymentDto) throws ResourceNotFoundException;

//    ResponseEntity<?> initiateBankTransfer(InitiateTransferDto transferDto, double amount);

//    ResponseEntity<?> initiateBankTransfer(InitiateTransferRequest transferDto);

//    Mono<ResponseEntity<?>> completeBankTransfer(String transferCode);

    ResponseEntity<?> initializeTransaction(InitiateTransferDto initiateTransferDto, Long loanRequestId) throws ResourceNotFoundException;

    ResponseEntity<BankAccount> verifyAccountDetailsAndSetup(CreateRecipientDto createRecipientDto);
}
