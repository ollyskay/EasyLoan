package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.api.InitiateTransfer.InitiateTransfer;
import com.example.easyloan.api.verifyaccount.VerifyAccount;
import com.example.easyloan.dto.CreateRecipientDto;
import com.example.easyloan.dto.InitiateTransferDto;
import com.example.easyloan.dto.TransferRequest;
import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.enums.PaymentMethod;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.model.*;
import com.example.easyloan.repository.*;
import com.example.easyloan.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentServiceImpl implements RepaymentService {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.paystack.co")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer sk_test_29d0c02151f8e2f3c0b99976ca2e78cc8cb0f03c")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                log.info("Request Headers: {}", clientRequest.headers());
                return Mono.just(clientRequest);
            }))
            .build();
    @Autowired
    private final PaymentServiceRepository paymentRepository;
    private final LoanServiceImpl loanService;
    private final BankRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final LoanRepaymentHistoryRepository loanRepaymentHistoryRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final String CALL_BACK_URL = "http://localhost:8090/verify_transaction";

    @Override
    public ResponseEntity<?> initializeTransaction(InitiateTransferDto initiateTransferDto, Long loanRequestId) throws ResourceNotFoundException {
        double amount = initiateTransferDto.getAmount();

        String reference = UUID.randomUUID().toString();

        // Fetch the user details based on the username (email in your case)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        LoanRequest loan = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));


        TransactionInfo paymentInfo = TransactionInfo.builder()
                .name(user.getFirstName())
                .email(user.getUsername())
                .amount(amount)
                .currency("NGN")
                .callBackUrl(CALL_BACK_URL)
                .date(new Date())
                .reference(reference)
                .build();

        try {
            InitiateTransfer initiateResponse = webClient
                    .post()
                    .uri("/transaction/initialize")
                    .bodyValue(paymentInfo)
                    .retrieve()
                    .bodyToMono(InitiateTransfer.class)
                    .block();

            if (initiateResponse != null) {
                log.info("Payment Info: {}", paymentInfo);
                paymentRepository.save(paymentInfo);

                ResponseEntity<?> transferResponse = initiateTransfer(initiateResponse, amount);
                if (transferResponse.getStatusCode().is2xxSuccessful()) {

                    LoanRepaymentHistory repaymentHistory = new LoanRepaymentHistory();
                    repaymentHistory.setPaymentAmount(amount);
                    repaymentHistory.setDate(new Date());
                    repaymentHistory.setPaymentMethod(PaymentMethod.PAYSTACK);
                    repaymentHistory.setDescription("Loan Repayment");
                    repaymentHistory.setLoan(loan);
                    repaymentHistory.setUser(user);

                    loanRepaymentHistoryRepository.save(repaymentHistory);

                    double initialRepaymentAmount = loan.getRepaymentAmount();
                    double paidAmount = amount;

                    double totalRepaymentAmount = calculateTotalRepaymentAmount(initialRepaymentAmount, paidAmount);
                    if (amount >= totalRepaymentAmount) {
                        loanService.updateLoanStatus(paymentInfo.getReference(), LoanStatus.PAID, loan);
                    } else {
                        loanService.updateLoanStatus(paymentInfo.getReference(), LoanStatus.RUNNING, loan);
                    }

                    return transferResponse;
                } else {
                    return new ResponseEntity<>("Error initiating transfer", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (WebClientResponseException e) {
            log.error("Error initiating payment: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getResponseBodyAsString().contains("You cannot initiate third party payouts as a starter business")) {

                return new ResponseEntity<>("Your fund transfer has been processed, please wait for the payment verification ", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Error initiating payment: " + e.getMessage(), e.getStatusCode());
            }
        }
        return new ResponseEntity<>("Payment type not available", HttpStatus.NOT_FOUND);

    }

    private ResponseEntity<?> initiateTransfer(InitiateTransfer initiateResponse, Double amount) {

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSource("balance");
        transferRequest.setReason("Loan Repayment");
        transferRequest.setAmount(amount);

        return webClient.post()
                .uri("/transfer")
                .bodyValue(transferRequest)
                .retrieve()
                .toEntity(InitiateTransfer.class)
                .map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.info("Transfer initiated successfully. Transfer ID: {}", responseEntity.getBody().getData());
                        return new ResponseEntity<>("Transfer initiated successfully", HttpStatus.OK);
                    } else {
                        log.error("Error initiating transfer: {} - {}", responseEntity.getStatusCode(),
                                responseEntity.getBody());
                        return new ResponseEntity<>("Error initiating transfer", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .block();
    }

    private double calculateTotalRepaymentAmount(Double initialRepaymentAmount, Double paidAmount) {
        if (initialRepaymentAmount == null) {
            return 0.0;
        }
        double remainingAmount = initialRepaymentAmount - (paidAmount != null ? paidAmount : 0.0);

        return Math.max(remainingAmount, 0.0);
    }

    @Override
    public ResponseEntity<BankAccount> verifyAccountDetailsAndSetup(CreateRecipientDto createRecipientDto) {
        try {
            Bank bank = bankRepository.findByName(createRecipientDto.getBankName())
                    .orElseThrow(() -> new ResourceNotFoundException("Bank not found"));

            VerifyAccount verifiedAccount = webClient.get()
                    .uri("/bank/resolve?account_number=" + createRecipientDto.getAccountNumber() + "&bank_code=" + bank.getCode())
                    .retrieve()
                    .bodyToMono(VerifyAccount.class)
                    .block();

            // Save the verified account details
            BankAccount savedBankAccount = saveVerifiedAccount(createRecipientDto, verifiedAccount);

            return new ResponseEntity<>(savedBankAccount, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred during account verification and setup: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BankAccount saveVerifiedAccount(CreateRecipientDto createRecipientDto, VerifyAccount verifiedAccount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

//        // Fetch the user by username
        Optional<User> user1 = userRepository.findByUsername(user.getUsername());
        User user2 = user1.get();

        // Create a new BankAccount entity and set its properties
        BankAccount bankAccount = BankAccount.builder()
                .bank(createRecipientDto.getBankName())
                .accountNumber(Long.valueOf(createRecipientDto.getAccountNumber()))
                .accountName(verifiedAccount.getData().getAccountName())
                .user(user2)
                .build();

        // Save the BankAccount entity to the database
        System.out.println("External API Response: " + verifiedAccount);
        return bankAccountRepository.save(bankAccount);
    }
}