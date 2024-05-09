package com.example.jwtacces.models.debt;

import jakarta.persistence.*;
import lombok.*;

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
    private Long creditorId;
    private Long debtorId;
    private Double amount;
    private String date;
    private String description;
    private Boolean isPaid;
}
