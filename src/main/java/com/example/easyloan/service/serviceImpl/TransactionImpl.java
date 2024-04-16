package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.model.User;
import com.example.easyloan.repository.TransactionRepository;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    @Override
    public BigDecimal getTotalAmountBorrowed(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return transactionRepository.sumTransactionAmount(currentUser);
    }



}


