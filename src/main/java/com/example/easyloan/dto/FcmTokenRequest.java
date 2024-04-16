package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FcmTokenRequest {
    private Long userId;
    private String fcmToken;
}
