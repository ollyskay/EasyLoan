package com.example.easyloan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EditUserProfileResponseDto {
    private String firstName;
    private String lastName;

}
