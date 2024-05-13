package com.example.jwtacces.DTO.debt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreateDebtDTO {
    @NotBlank
    private String debtorUsername;
    @NotNull
    @Positive
    private Double amount;
    @NotBlank
    private String description;
    @NotNull
    private Boolean isPaid;
}
