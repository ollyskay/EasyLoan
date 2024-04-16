package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.dto.ApiResponse;
import com.example.easyloan.dto.LoanOfferDTO;
import com.example.easyloan.dto.LoanOfferResponse;
import com.example.easyloan.mapper.LoanMapper;
import com.example.easyloan.model.LoanOffer;
import com.example.easyloan.model.User;
import com.example.easyloan.repository.LoanOfferRepository;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.LoanOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoanOfferServiceImpl implements LoanOfferService {
    private final LoanOfferRepository loanOfferRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<LoanOffer>> createLoanOffer(LoanOfferDTO loanOfferDTO) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            LoanOffer loanOffer = new LoanOffer();
            loanOffer.setOfferAmount(loanOfferDTO.getOfferAmount());
            loanOffer.setDate(loanOfferDTO.getDate());
            loanOffer.setDurationInDays(loanOfferDTO.getDurationInDays());
            loanOffer.setInterestRate(loanOfferDTO.getInterestRate());
            loanOffer.setPaymentMethod(loanOfferDTO.getPaymentMethod());
            loanOffer.setDescription(loanOfferDTO.getDescription());
            loanOffer.setBorrowerId(loanOfferDTO.getBorrowerId());
            loanOffer.setSuccessful(loanOfferDTO.isSuccessful());
            loanOffer.setDateCollected(loanOfferDTO.getDateCollected());
            loanOffer.setInterestRate(loanOfferDTO.getInterestRate());
            loanOffer.setUser(currentUser);
            loanOffer.setSuccessful(true);
            LoanOffer createdLoanOffer = loanOfferRepository.save(loanOffer);

            if (createdLoanOffer != null) {
                ApiResponse<LoanOffer> response = new ApiResponse<>("200",
                        "Loan offer created successfully", createdLoanOffer,HttpStatus.OK);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                throw new RuntimeException("Failed to create loan offer");
            }
        }return (ResponseEntity<ApiResponse<LoanOffer>>) ResponseEntity.ok();
    }

    public ApiResponse<List<LoanOfferResponse>> getLoanOffersByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<User> userOptional = userRepository.findByUsername(currentUser.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            log.info("Checking if loan offers exist for user: {}", user.getUsername());

            List<LoanOffer> fetchAllLoanOffer = loanOfferRepository.findByUser(user);

            if (fetchAllLoanOffer.isEmpty()) {
                log.info("User {} doesn't have any loan offers", user.getUsername());
                return new ApiResponse<>("10", "Loan offers not found", HttpStatus.NOT_FOUND);
            }

            List<LoanOfferResponse> loanOfferResponses = new ArrayList<>();

            for (LoanOffer loanOffer : fetchAllLoanOffer) {
                LoanOfferResponse loanOfferResponse = new LoanOfferResponse();
                LoanMapper.mapToLoanOfferResponse(loanOffer, loanOfferResponse);
                loanOfferResponses.add(loanOfferResponse);
            }

            log.info("Loan offers found for user: {}", user.getUsername());

            return new ApiResponse<>("200", "Loan offers found", loanOfferResponses, HttpStatus.OK);
        } else {
            return new ApiResponse<>("10", "User not found", HttpStatus.NOT_FOUND);
        }
    }
}