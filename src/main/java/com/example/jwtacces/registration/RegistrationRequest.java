package com.example.jwtacces.registration;

import com.example.jwtacces.models.userEntity.RoleEntity;

import java.util.Set;

public record RegistrationRequest(String username, String password, String email, Set<RoleEntity> roles){
}
