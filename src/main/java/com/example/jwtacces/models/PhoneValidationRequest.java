package com.example.jwtacces.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneValidationRequest {
    @NotBlank
    private String phone;
    @NotNull
    private long verificationCode;
}
