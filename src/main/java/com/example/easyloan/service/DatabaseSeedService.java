package com.example.easyloan.service;

import org.springframework.http.ResponseEntity;

public interface DatabaseSeedService {
    ResponseEntity<?> seedBanks();
}
