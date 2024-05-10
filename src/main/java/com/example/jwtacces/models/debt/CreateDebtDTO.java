package com.example.jwtacces.models.debt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreateDebtDTO {
    private String debtorUsername;
    private Double amount;
    private String description;
    private Boolean isPaid;
}
