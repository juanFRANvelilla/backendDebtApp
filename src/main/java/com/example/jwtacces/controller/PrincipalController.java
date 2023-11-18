package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.*;
import com.example.jwtacces.repository.PhoneValidationRepository;
import com.example.jwtacces.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    @PostMapping(path = "/validatePhone")
    public ResponseEntity<?>validatePhone(@Valid @RequestBody PhoneValidationRequest phoneValidationRequest){
        PhoneValidation phoneValidation = phoneValidationRepository.findPhoneValidationByPhone(phoneValidationRequest.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de servidor, vuelve a intentar el registro"));
        if (phoneValidation.getVerificationCode() == phoneValidationRequest.getVerificationCode()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime requestData = LocalDateTime.parse(phoneValidation.getRequestData(), formatter);
            LocalDateTime currentTime = LocalDateTime.now();
            if (requestData.plusMinutes(10).isAfter(currentTime)) {
                phoneValidationRepository.setPhoneValidationTrue(phoneValidationRequest.getPhone());
                return ResponseEntity.ok("Codigo valido, tu numero se ha registrado correctamente");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A caducado tu tiempo para introducir el codigo");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Codigo no valido");

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
