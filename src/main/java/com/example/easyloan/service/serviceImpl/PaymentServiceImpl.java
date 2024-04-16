package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.api.createRecipient.CreateRecipient;
import com.example.easyloan.api.paystackPaymentInit.PaystackResponse;
import com.example.easyloan.api.paystackpaymentverify.PaymentVerificationResponse;
import com.example.easyloan.api.verifyaccount.VerifyAccount;
import com.example.easyloan.dto.*;
import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.enums.PaymentMethod;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.model.Bank;
import com.example.easyloan.model.LoanRequest;
import com.example.easyloan.model.TransactionInfo;
import com.example.easyloan.repository.BankRepository;
import com.example.easyloan.repository.LoanRequestRepository;
import com.example.easyloan.repository.PaymentServiceRepository;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.paystack.co")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer sk_test_29d0c02151f8e2f3c0b99976ca2e78cc8cb0f03c")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                log.info("Request Headers: {}", clientRequest.headers());
                return Mono.just(clientRequest);
            }))
            .build();

    private final PaymentServiceRepository paymentRepository;
    private final BankRepository bankRepository;
    private final LoanServiceImpl loanService;
    private final EmailServiceImpl emailService;
    private final LoanRequestRepository loanRequestRepository;
    private final String CALL_BACK_URL = "http://localhost:8090/verify_transaction";
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> initializeTransaction(LenderLoanDisbursementDto loanDisbursementDto) throws ResourceNotFoundException {
        double amount = 0;
        amount = calculateTotalLoanAmount(loanDisbursementDto.getDisbursementPayments());

        String reference = UUID.randomUUID().toString();
        TransactionInfo paymentInfo = TransactionInfo.builder()
                .name(loanDisbursementDto.getName())
                .email(loanDisbursementDto.getUsername())
                .amount(amount)
                .currency("NGN")
                .callBackUrl(CALL_BACK_URL)
                .date(new Date())
                .reference(reference)
                .build();

        if (loanDisbursementDto.getPaymentMethod().equals(PaymentMethod.PAYSTACK)) {

            try {
                PaystackResponse initiateResponse = webClient
                        .post()
                        .uri("/transaction/initialize")
                        .bodyValue(paymentInfo)
                        .retrieve()
                        .bodyToMono(PaystackResponse.class)
                        .block();


                if (initiateResponse != null) {
                    paymentRepository.save(paymentInfo);
                    return new ResponseEntity<>(initiateResponse, HttpStatus.OK);
                }

            } catch (WebClientResponseException e) {
                return new ResponseEntity<>("Error initiating payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("Payment type not available", HttpStatus.NOT_FOUND);
    }

    private double calculateTotalLoanAmount(List<LoanDisbursementDto> lenderPayments) {
        if(lenderPayments == null){
            return 0.0;
        }
        return lenderPayments.stream()
                .mapToDouble(LoanDisbursementDto::getLoanAmount)
                .sum();
    }

    @Override
    public ResponseEntity<PaymentVerificationResponse> verifyTransaction(String reference) throws ResourceNotFoundException {
        TransactionInfo transactionInfo = paymentRepository.findByReference(reference).orElseThrow(
                () -> new ResourceNotFoundException("Transaction not found")
        );

        try {
            PaymentVerificationResponse verificationResponse = webClient
                    .get()
                    .uri("/transaction/verify/" + reference)
                    .retrieve()
                    .bodyToMono(PaymentVerificationResponse.class)
                    .block();

            if (verificationResponse != null) {
                if ("success".equals(verificationResponse.getData().getStatus())) {
                    // Update the status of the transaction in your database
                    transactionInfo.setVerified(true);
                    paymentRepository.save(transactionInfo);

                    return new ResponseEntity<>(verificationResponse, HttpStatus.OK);
                } else {
                    // Handle other status scenarios if needed
                    return new ResponseEntity<>(verificationResponse, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (WebClientResponseException e) {
            // Handle WebClient exceptions, log or return an error response
            String errorMessage = "Error verifying payment: " + e.getMessage();
            log.error(errorMessage);

            return new ResponseEntity<>(new PaymentVerificationResponse(false, errorMessage, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new PaymentVerificationResponse(false, "Error verifying payment", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<BankNameResponse> getAllBanks() {
        List<Bank> bankList = bankRepository.findAll();
        return new ResponseEntity<>(BankNameResponse.builder()
                .bankNames(bankList.stream().map(this::bankToString).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createRecipient(CreateRecipientDto createRecipientDto) {
        try {
            log.info("Received CreateRecipientDto: {}", createRecipientDto);

            String bankName = createRecipientDto.getBankName();
            if (bankName == null || bankName.trim().isEmpty()) {
                return new ResponseEntity<>("Bank name is required", HttpStatus.BAD_REQUEST);
            }

            Bank bank = bankRepository.findByName(bankName)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank not found"));

            ResponseEntity<VerifyAccount> verifiedAccountResponse = verifyAccountDetails(createRecipientDto);

            if (verifiedAccountResponse.getStatusCode().is2xxSuccessful()) {
                String accountName = verifiedAccountResponse.getBody().getData().getAccountName();

                RecipientDto recipientDto = RecipientDto.builder()
                        .type("nuban")
                        .name(accountName)
                        .account_number(createRecipientDto.getAccountNumber())
                        .bank_code(bank.getCode())
                        .currency("NGN")
                        .build();

                try {
                    ResponseEntity<RecipientDto> responseEntity = webClient.post()
                            .uri("/transferRecipient")
                            .bodyValue(recipientDto)
                            .retrieve()
                            .toEntity(RecipientDto.class)
                            .block();

                    if (responseEntity != null) {
                        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (WebClientResponseException e) {
                    log.error("WebClient error creating recipient: {}", e.getMessage());
                    return handleErrorResponse(e, "Error creating recipient");
                } catch (Exception e) {
                    log.error("Error creating recipient: {}", e.getMessage());
                    return handleErrorResponse(e, "Error creating recipient");
                }
            } else {
                return new ResponseEntity<>(verifiedAccountResponse.getBody(), verifiedAccountResponse.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error creating recipient: {}", e.getMessage());
            return handleErrorResponse(e, "Error creating recipient");
        }
    }

    private Mono<CreateRecipient> handleWebClientResponseException(WebClientResponseException e) {
        log.error("Error creating recipient. Status: {}, Response: {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        return Mono.empty();
    }


    private ResponseEntity<VerifyAccount> verifyAccountDetails(CreateRecipientDto createRecipientDto) throws ResourceNotFoundException {
        Bank bank = bankRepository.findByName(createRecipientDto.getBankName()).orElseThrow(
                () -> new ResourceNotFoundException("Bank not found"));
        VerifyAccount verifiedAccount = webClient.get()
                .uri("/bank/resolve?account_number=" + createRecipientDto.getAccountNumber() + "&bank_code=" + bank.getCode())
                .retrieve()
                .bodyToMono(VerifyAccount.class)
                .block();
        return new ResponseEntity<>(verifiedAccount, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> disburseLoan(DisbursementRequest disbursementRequest) {
        try {
            validateLoanDisbursementDto(disbursementRequest.getLoanDisbursementDto());

//            Bank bank = getBankDetails(loanDisbursementDto.getBankName());
            LoanRequest loanRequest;
            ResponseEntity<?> createRecipientResponse = createRecipient(disbursementRequest.getCreateRecipientDto());
            if (createRecipientResponse.getStatusCode().is2xxSuccessful()) {
                TransactionInfo transactionInfo = buildTransactionInfo(disbursementRequest.getLoanDisbursementDto());

                ResponseEntity<PaystackResponse> initiateResponse = initiateLoanDisbursement(transactionInfo);

                if (initiateResponse.getStatusCode().is2xxSuccessful()) {
                    ResponseEntity<PaymentVerificationResponse> verificationResponse = verifyTransaction(transactionInfo.getReference());

                    if (verificationResponse.getStatusCode().is2xxSuccessful()) {
                        Optional<LoanRequest> loanRequest1 = loanRequestRepository.findByReference(transactionInfo.getReference());
                        loanRequest = loanRequest1.get();
                        loanService.updateLoanStatus(transactionInfo.getReference(), LoanStatus.DISBURSED, loanRequest);

                        notifyBorrower(transactionInfo);

                        return new ResponseEntity<>(initiateResponse.getBody(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Error verifying loan disbursement", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Error initiating loan disbursement", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Error creating recipient", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (ResourceNotFoundException e) {
            return handleErrorResponse(e, "Resource not found during loan disbursement initiation");
        } catch (Exception e) {
            return handleErrorResponse(e, "Error during loan disbursement initiation");
        }
    }


    public ResponseEntity<?> handleWebClientErrorResponse(WebClientResponseException e, String errorMessage) {
        log.error("{}: {}", errorMessage, e.getMessage());

        return ResponseEntity.status(e.getRawStatusCode())
                .body("Error during loan disbursement initiation: " + e.getResponseBodyAsString());
    }

    private ResponseEntity<?> handleErrorResponse(Exception e, String errorMessage) {
        log.error("{}: {}", errorMessage, e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error during loan disbursement initiation: " + e.getMessage());
    }


    private ResponseEntity<?> handleInitiateLoanResponse(ResponseEntity<PaystackResponse> initiateResponse, TransactionInfo transactionInfo) {
        if (initiateResponse.getStatusCode().is2xxSuccessful()) {
            // Optionally save transactionInfo in your database
            paymentRepository.save(transactionInfo);
            return new ResponseEntity<>(initiateResponse.getBody(), HttpStatus.OK);
        } else {
            // Handle initiation failure
            return new ResponseEntity<>("Error initiating loan disbursement", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateLoanDisbursementDto(LoanDisbursementDto loanDisbursementDto) {
        if (loanDisbursementDto == null) {
            throw new IllegalArgumentException("Loan disbursement DTO cannot be null");
        }

        // Perform other validation checks based on your requirements
        if (loanDisbursementDto.getLoanAmount() <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than zero");
        }

    }

    private Bank getBankDetails(String bankName) {
        // Validate bankName
        if (bankName == null || bankName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bank name cannot be null or empty");
        }
        return bankRepository.findByName(bankName)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found"));
    }

    private void notifyBorrower(TransactionInfo transactionInfo) {
        String borrowerEmail = transactionInfo.getEmail();
        String loanReference = transactionInfo.getReference();

        emailService.sendEmail(borrowerEmail,
                "Your requested loan has been successfully disbursed to the provided account" + loanReference,
                "Loan Disbursement Message");
    }


    private TransactionInfo buildTransactionInfo(LoanDisbursementDto loanDisbursementDto) {
        return TransactionInfo.builder()
                .name(loanDisbursementDto.getBorrowerName())
                .email(loanDisbursementDto.getBorrowerUsername())
                .amount(loanDisbursementDto.getLoanAmount())
                .currency("NGN")
                .callBackUrl(CALL_BACK_URL)
                .date(new Date())
                .reference(UUID.randomUUID().toString())
                .build();
    }


    private ResponseEntity<PaystackResponse> initiateLoanDisbursement(TransactionInfo transactionInfo) {
        return webClient.post()
                .uri("/transaction/initialize")
                .bodyValue(transactionInfo)
                .retrieve()
                .bodyToMono(PaystackResponse.class)
//                .onErrorResume(WebClientResponseException.class, this::handleWebClientResponseException)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)))
                .block();
    }

    private String bankToString(Bank bank) {
        return bank.getName();
    }
}
