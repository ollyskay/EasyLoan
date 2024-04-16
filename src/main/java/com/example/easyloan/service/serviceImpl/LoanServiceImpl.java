package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.dto.LendingOfferDto;
import com.example.easyloan.dto.LoanRequestDTO;
import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.exception.BadRequestException;
import com.example.easyloan.exception.ResourceNotFoundException;
import com.example.easyloan.exception.UserLoanNotFoundException;
import com.example.easyloan.model.*;
import com.example.easyloan.repository.EarningRepository;
import com.example.easyloan.repository.LoanOfferRepository;
import com.example.easyloan.repository.LoanRequestRepository;
import com.example.easyloan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRequestRepository loanRequestRepository;
    private final LoanOfferRepository loanOfferRepository;
    private final EarningRepository earningRepository;
    @Override
    //ACCEPT LOAN REQUEST
    public String loanRequest(LoanRequestDTO loanRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser != null) {
            loanRequestDTO.setUser(currentUser);
            LoanRequest loanRequest = convertToLoanRequest(loanRequestDTO);
            loanRequestRepository.save(loanRequest);
            return "Loan request successfully created.";
        } else {
            throw new BadRequestException("Unauthorized request, please log in", HttpStatus.UNAUTHORIZED);
        }
    }
 
    //NOT NEEDED
    @Override
    public String acceptLoan(Long loanRequestId) {
        Optional<LoanRequest> optionalLoanRequest = loanRequestRepository.findById(loanRequestId);

        if (optionalLoanRequest.isPresent()) {
            LoanRequest loanRequest = optionalLoanRequest.get();

            if (!loanRequest.isAcceptedByBorrower()) {
                loanRequest.setAcceptedByBorrower(true);
                loanRequestRepository.save(loanRequest);
                return "Loan accepted by borrower.";
            } else {
                return "Loan already accepted by borrower.";
            }
        } else {
            throw new NotFoundException("Loan request not found.");
        }
    }


    @Override
    public List<LendingOfferDto> lendingOffers(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return loanOfferRepository.findAllByOfferStatus("open",pageable);
    }


    private LoanRequest convertToLoanRequest(LoanRequestDTO loanRequestDTO) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(loanRequestDTO.getLoanAmount());
        loanRequest.setPurpose(loanRequestDTO.getPurpose());
        loanRequest.setDate(loanRequestDTO.getDate());
        loanRequest.setInterestRate(loanRequestDTO.getInterestRate());
        loanRequest.setUser(loanRequestDTO.getUser());
        loanRequest.setSupportingDocument(loanRequestDTO.getSupportingDocument());
        return loanRequest;
    }

        @Override
        public Double getOutstandingBalance(Long userId, LoanStatus loanStatus) {
            Optional<LoanRequest> acceptedLoanRequest = loanRequestRepository.findByUserIdAndLoanStatus(userId, loanStatus);

            if (acceptedLoanRequest.isEmpty()) {
                throw new UserLoanNotFoundException("User with ID " + userId + " does not have a running loan.");
            }
            double outstandingBalance = acceptedLoanRequest.get().getOutstandingBalance();

            return Math.max(outstandingBalance, 0.0);
        }

        @Override
    public void updateLoanStatus(String reference, LoanStatus newStatus, LoanRequest loan) {
            loan.setLoanStatus(newStatus);
           loanRequestRepository.save(loan);
//        Optional<LoanRequest> optionalLoan = loanRequestRepository.findByReference(reference);

//        if (optionalLoan.isPresent()) {
//            LoanRequest loan2 = optionalLoan.get();
//            loan.setLoanStatus(newStatus);
//            loanRequestRepository.save(loan2);
//        } else {
//            // Handle case where loan with the given reference is not found
//            throw new ResourceNotFoundException("Loan not found with reference: " + reference);
//        }
    }


    public Double getLenderEarnings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<LoanRequest> loanRequest = loanRequestRepository.findByUser(currentUser);

        if (loanRequest.isEmpty()) {
            throw new UserLoanNotFoundException("User with ID " + currentUser.getId() + " does not have a running loan.");
        }

        LoanRequest loanRequest1 = loanRequest.get();
        LoanStatus loanStatus = loanRequest1.getLoanStatus();

        if (loanStatus == LoanStatus.APPROVED) {
            double outstandingBalance = loanRequest1.getOutstandingBalance();
            double interestRate = loanRequest1.getInterestRate();
            double earnings = outstandingBalance * (interestRate / 100);

            Earning earning = new Earning();
            earning.setMonthlyEarnings((long) earnings);


            earning.setEarningDate(LocalDate.now());


            earningRepository.save(earning);

            return earnings;
        } else {
            throw new UserLoanNotApprovedException("User loan is not approved.", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    public List<Earning> getMonthlyEarningsByDate(LocalDate date) {
        return earningRepository.findByEarningDate(date);
    }

    public String approveLoanRequest(Long loanRequestId){

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("The requested loan with the provided id number " + loanRequestId + " is not found"));

        updateLoanStatus("Your loan has been successfully approved and will be disbursed into your account shortly",
                LoanStatus.APPROVED, loanRequest);

        loanRequestRepository.save(loanRequest);

        LoanOffer loanOffer = loanRequest.getLoanOffer();
        loanOffer.setOfferStatus("approved");
        loanOfferRepository.save(loanOffer);

        return loanRequest.getUser().getFirstName() + " " + loanRequest.getUser().getLastName();
    }


    @Override
    public Page<LoanRequest> getAllLoanRequests(Integer pageNo, Integer pageSize) {
        if (pageNo < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return loanRequestRepository.findAll(pageable);
    }



    }





















