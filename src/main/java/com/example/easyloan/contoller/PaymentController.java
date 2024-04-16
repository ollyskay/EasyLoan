package com.example.easyloan.contoller;


import com.example.easyloan.api.paystackpaymentverify.PaymentVerificationResponse;
import com.example.easyloan.dto.BankNameResponse;
import com.example.easyloan.dto.CreateRecipientDto;
import com.example.easyloan.dto.DisbursementRequest;
import com.example.easyloan.dto.LenderLoanDisbursementDto;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.service.serviceImpl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loanDbt")
public class PaymentController {

    @Autowired
    private final PaymentServiceImpl paymentService;


    @PostMapping("/initialize/transaction")
    public ResponseEntity<?> initializeTransaction(@RequestBody LenderLoanDisbursementDto lenderLoanDisbursementDto) {
        try {
            return paymentService.initializeTransaction(lenderLoanDisbursementDto);
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "Resource not found during loan initialization");
        } catch (Exception e) {
            return handleException(e, "Error during loan initialization");
        }
    }

    private ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e, String errorMessage) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorMessage + ": " + e.getMessage());
    }

    private ResponseEntity<?> handleException(Exception e, String errorMessage) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage + ": " + e.getMessage());
    }

    @GetMapping("/verify-transaction/{reference}")
    public ResponseEntity<PaymentVerificationResponse> verifyTransaction(@PathVariable String reference) {
        try {
            return paymentService.verifyTransaction(reference);
        } catch (ResourceNotFoundException e) {
            return (ResponseEntity<PaymentVerificationResponse>) handleResourceNotFoundException(e, "Transaction not found");
        }
    }

    @GetMapping("/list-of-banks")
    public ResponseEntity<BankNameResponse> getAllBanks() {
        return paymentService.getAllBanks();
    }

    @PostMapping("/create-recipient")
    public ResponseEntity<?> createRecipient(@RequestBody CreateRecipientDto createRecipientDto) throws ResourceNotFoundException {
        return paymentService.createRecipient(createRecipientDto);
    }

    @PostMapping("/disburse-loan")
    public ResponseEntity<?> disburseLoan(@RequestBody DisbursementRequest disbursementRequest) {
        try {
            return paymentService.disburseLoan(disbursementRequest);
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "Resource not found during loan disbursement");
        } catch (Exception e) {
            return handleException(e, "Error during loan disbursement");
        }
    }

}
