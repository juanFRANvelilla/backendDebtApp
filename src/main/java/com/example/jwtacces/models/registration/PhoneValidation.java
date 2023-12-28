package com.example.jwtacces.models.registration;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "phone_validations")
public class PhoneValidation {
    @Id
    @SequenceGenerator(name = "idValidations",
            sequenceName = "idValidations",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idValidations")
    private int id;
    @NotBlank
    private String username;
    @NotNull
    private long verificationCode;
    private String requestData;
    private int attempts;
    private boolean valid;
}
