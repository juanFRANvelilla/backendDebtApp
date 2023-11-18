package com.example.jwtacces.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneValidationRequest {
    @NotBlank
    private int id;
    @NotBlank
    private String phone;
    @NotBlank
    private long verificationCode;
    @NotBlank
    private String requestData;
}
