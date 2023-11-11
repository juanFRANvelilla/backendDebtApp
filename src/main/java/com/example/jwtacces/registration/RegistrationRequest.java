package com.example.jwtacces.registration;

import com.example.jwtacces.models.RoleEntity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

public record RegistrationRequest(String username, String password, String email, Set<RoleEntity> roles){
}
