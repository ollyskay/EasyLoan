package com.example.easyloan.service;


import com.example.easyloan.dto.DocumentResponseDto;

public interface DocumentService {
    DocumentResponseDto getUserDocuments();
    void deleteDocuments(String documentType);
}
