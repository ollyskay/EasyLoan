package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EditUserProfileRequestDto {
    private String firstName;
    private String lastName;
}
