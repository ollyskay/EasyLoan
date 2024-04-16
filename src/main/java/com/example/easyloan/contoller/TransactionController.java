package com.example.easyloan.contoller;

import com.example.easyloan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/borrowed-amount")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;


    @GetMapping("/total-amount")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getTotalAmountBorrowed() {
        log.info("Request Received");
        return transactionService.getTotalAmountBorrowed();
    }

//    @PostMapping("/create-transaction")
//    public ResponseEntity<TransactionRequest> createTransaction(@RequestBody TransactionRequest request){
//        transactionService.createTransaction(request);
//        return new ResponseEntity<>(request,HttpStatus.CREATED);
//    }

}

