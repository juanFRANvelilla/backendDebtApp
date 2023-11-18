package com.example.jwtacces.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String phone;
    @NotBlank
    private long verificationCode;
    @NotBlank
    private String requestData;
    @NotBlank
    private boolean valid;
}
