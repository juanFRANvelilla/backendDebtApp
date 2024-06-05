package com.example.jwtacces.models.debt;

import com.example.jwtacces.models.userEntity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="debt")
@Getter
@Builder
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "creditor_id")
    private UserEntity creditor;
    @ManyToOne
    @JoinColumn(name = "debtor_id")
    private UserEntity debtor;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private Boolean isPaid;
}
