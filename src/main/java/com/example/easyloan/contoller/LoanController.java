package com.example.easyloan.contoller;


import com.example.easyloan.dto.ApproveLoanDTO;
import com.example.easyloan.dto.ApproveLoanResponse;
import com.example.easyloan.dto.LendingOfferDto;
import com.example.easyloan.dto.LoanRequestDTO;
import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.exception.BadRequestException;
import com.example.easyloan.exception.UserLoanNotFoundException;
import com.example.easyloan.model.Earning;
import com.example.easyloan.model.LoanRequest;
import com.example.easyloan.service.serviceImpl.LoanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loan")
public class LoanController {

    private final LoanServiceImpl loanService;

    @Autowired
    public LoanController(LoanServiceImpl loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/loan-request")
    public ResponseEntity<String> requestLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        try {
            String response = loanService.loanRequest(loanRequestDTO);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/accept-loan/{loanRequestId}")
    public ResponseEntity<String> acceptLoan(@PathVariable Long loanRequestId) {
        String response = loanService.acceptLoan(loanRequestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/loan-offers")
    public ResponseEntity<List<LendingOfferDto>> lendingOffers(
            @RequestParam Integer pageNo,
            @RequestParam Integer pageSize
    ) {
        return new ResponseEntity<>(loanService.lendingOffers(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/outstanding-balance/{userId}")
    public ResponseEntity<Double> getOutstandingBalance(@PathVariable Long userId, LoanStatus loanStatus) {
        try {
            Double outstandingBalance = loanService.getOutstandingBalance(userId, loanStatus);
            return ResponseEntity.ok(outstandingBalance);
        } catch (UserLoanNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-earnings")
    public ResponseEntity<Double> calculateLenderEarnings() {
        try {
            Double interestAmount = loanService.getLenderEarnings();
            return new ResponseEntity<>(interestAmount, HttpStatus.OK);
        } catch (UserLoanNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter-earnings")
    public ResponseEntity<List<Earning>> filterEarnings(@RequestParam LocalDate date) {
        List<Earning> filteredEarnings = loanService.getMonthlyEarningsByDate(date);
        return ResponseEntity.ok().body(filteredEarnings);
    }

    @PutMapping("/approveLoan")
    public ResponseEntity<?> approveLoanRequest(@RequestParam ApproveLoanDTO approveLoanDTO) {
        String fullName = loanService.approveLoanRequest(approveLoanDTO.getLoanRequestId());
        ApproveLoanResponse approveLoanResponse = new ApproveLoanResponse();
        approveLoanResponse.setFullName(fullName);
        approveLoanResponse.setStatusCode(1);
        approveLoanResponse.setMessage("Your loan has been approved successfully");
        return ResponseEntity.ok(approveLoanResponse);

    }

    @GetMapping("/loan-requests-list")
    @PreAuthorize("hasRole('ROLE_LENDER')")
    public ResponseEntity<Page<LoanRequest>> getAllLoanRequests(
            @RequestParam Integer pageNo,
            @RequestParam Integer pageSize
    ) {
        try {
            Page<LoanRequest> loanRequestsPage = loanService.getAllLoanRequests(pageNo, pageSize);
            return new ResponseEntity<>(loanRequestsPage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    }



