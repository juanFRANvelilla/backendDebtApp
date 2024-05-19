package com.example.jwtacces.models.notification;

import com.example.jwtacces.models.debt.Debt;
import com.example.jwtacces.models.userEntity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="debt_notification")
@Getter
@Builder
public class DebtNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;
}
