package com.example.jwtacces.models.debt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

@Getter
public class CreateDebtDTO {
    private String debtorUsername;
    private Double amount;
    private String description;
}
