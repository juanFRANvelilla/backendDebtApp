package com.example.jwtacces.DTO.notification;

import com.example.jwtacces.DTO.debt.DebtDTO;
import com.example.jwtacces.DTO.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DebtNotificationDTO {
    private DebtDTO debt;
    private String date;
}
