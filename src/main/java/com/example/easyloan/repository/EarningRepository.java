package com.example.easyloan.repository;

import com.example.easyloan.model.Earning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EarningRepository extends JpaRepository<Earning, Long> {
    List<Earning> findByEarningDate(LocalDate date);
}
