package com.example.easyloan.repository;

import com.example.easyloan.model.Transaction;
import com.example.easyloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("SELECT sum(t.transactionAmount) FROM Transaction t WHERE t.user =  :userId And t.transactionAmount != null")
    BigDecimal sumTransactionAmount(User userId);

}
