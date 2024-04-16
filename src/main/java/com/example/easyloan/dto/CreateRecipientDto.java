package com.example.easyloan.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRecipientDto {
    @NotBlank(message = "Bank name can not be blank, please enter the Bank name attached to this account number")
    private String bankName;
    @NotBlank(message = "Account number can not be blank, please enter the your account number")
    @NotNull(message = "Account number can not be null, please enter your account number")
    private String accountNumber;
}

