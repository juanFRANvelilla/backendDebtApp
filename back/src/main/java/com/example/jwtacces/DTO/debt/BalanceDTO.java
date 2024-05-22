package com.example.jwtacces.DTO.debt;

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
