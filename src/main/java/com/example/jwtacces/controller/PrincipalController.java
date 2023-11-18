package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.*;
import com.example.jwtacces.repository.PhoneValidationRepository;
import com.example.jwtacces.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneValidationRepository phoneValidationRepository;

    @PostMapping(path = "/confirmPhone")
    public ResponseEntity<?>confirmPhone(@Valid @RequestBody PhoneValidation phoneValidation){
        Optional<PhoneValidation> phoneValidationOptional = phoneValidationRepository.findPhoneValidationByPhone(phoneValidation.getPhone());
        if (phoneValidationOptional.isPresent() && phoneValidationOptional.get().isValid()){
            return ResponseEntity.ok("El número de teléfono esta registrado ya");
        }
        phoneValidationRepository.save(phoneValidation);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El número de teléfono no existe o no está confirmado.");
    }

    @PostMapping(path = "/validatePhone")
    public ResponseEntity<?>validatePhone(@Valid @RequestBody PhoneValidationRequest phoneValidationRequest){
        PhoneValidation phoneValidation = phoneValidationRepository.findPhoneValidationByPhone(phoneValidationRequest.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Not found number reference"));
        if (phoneValidation.getVerificationCode() == phoneValidationRequest.getVerificationCode() {
            if(Integer.parseInt(phoneValidationRequest.getRequestData())< Integer.parseInt(phoneValidation.getRequestData() + 10)){
                return ResponseEntity.ok("Success to register the new phone");
            }
            else{
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fecha");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El código no coincide");

    }


    @PostMapping(path = "/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        Set<RoleEntity> roles = new HashSet<RoleEntity>();
        RoleEntity userRole = RoleEntity.builder()
                                        .name(ERole.valueOf("USER"))
                                        .build();
        roles.add(userRole);

        UserEntity newUser = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .phone(createUserDTO.getPhone())
                .roles(roles)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping(path = "/deleteUser")
    public void deleteUser(@RequestParam String id){
        userRepository.deleteById(Integer.parseInt(id));
    }
}
