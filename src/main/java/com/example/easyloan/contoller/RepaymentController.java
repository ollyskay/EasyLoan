package com.example.easyloan.contoller;

import com.example.easyloan.dto.CreateRecipientDto;
import com.example.easyloan.dto.InitiateTransferDto;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.model.BankAccount;
import com.example.easyloan.service.serviceImpl.RepaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan-repayment")
@RequiredArgsConstructor
@Slf4j
public class RepaymentController {

    private final RepaymentServiceImpl repaymentService;


    @PostMapping("/initialize/{loanRequestId}")
    public ResponseEntity<?> initializeTransaction(@RequestBody InitiateTransferDto initiateTransferDto, @PathVariable Long loanRequestId) {
        try {
            return repaymentService.initializeTransaction(initiateTransferDto, loanRequestId);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/verify-and-setup")
    public ResponseEntity<BankAccount> verifyAccountDetailsAndSetup(@RequestBody CreateRecipientDto createRecipientDto) {
        try {
            ResponseEntity<BankAccount> responseEntity = repaymentService.verifyAccountDetailsAndSetup(createRecipientDto);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (Exception e) {
            log.error("An error occurred in verifyAccountDetailsAndSetup: " + e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}

