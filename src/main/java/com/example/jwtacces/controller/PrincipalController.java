package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.ERole;
import com.example.jwtacces.models.RoleEntity;
import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/start")
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/hello")
    public String saludar(){
        return "hola";
    }

    @GetMapping(path = "/hello2")
    public String saludarSecreto(){
        return "hola secreto";
    }


    @PostMapping(path = "createUser")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        Set<RoleEntity> roles = createUserDTO.getRoles()
                .stream()
                .map(role -> RoleEntity.builder()
                                        .name(ERole.valueOf(role))
                                        .build())
                                            .collect(Collectors.toSet());

        UserEntity user = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .roles(roles)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(path = "/deleteUser")
    public void deleteUser(@RequestParam String id){
        userRepository.deleteById(Integer.parseInt(id));
    }
}
