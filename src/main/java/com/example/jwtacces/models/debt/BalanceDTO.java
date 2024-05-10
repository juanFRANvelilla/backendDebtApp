package com.example.jwtacces.models.debt;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class BalanceDTO {
    private Double owe;
    private Double owed;
}
