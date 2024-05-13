package com.example.jwtacces.models.debt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DebtDTO {
    private boolean isCreditor;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private Boolean isPaid;
}
