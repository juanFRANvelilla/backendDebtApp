package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.*;
import com.example.jwtacces.models.registration.PhoneValidation;
import com.example.jwtacces.repository.registration.PhoneValidationRepository;
import com.example.jwtacces.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(path = "/api")
public class AccesController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneValidationRepository phoneValidationRepository;

    /*
    verifica que el telefono con el que se quiere registrar un nuevo usuario no este ya siendo
    utilizado en la app, se manda un codigo aleatorio creado en el front que se mandara por sms
    que luego se tendra que validar
    unicamente creara una fila en la bd con el nuevo numero a registrar y el codigo que tiene
    que ser validado, en la siguiente llamada es cuando si el codigo corresponde se agregan todos
    los datos del user a la aplicacion
     */
    @PostMapping(path = "/confirmPhone")
    public ResponseEntity<?> confirmPhone(@Valid @RequestBody PhoneValidation phoneValidation) {
        Map<String, Object> httpResponse = new HashMap<>();
        Optional<PhoneValidation> phoneValidationOptional = phoneValidationRepository.findPhoneValidationByPhone(phoneValidation.getUsername());

        if (phoneValidationOptional.isPresent() && phoneValidationOptional.get().isValid()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        //agregamos un nuevo registro de validacion de telefono con la fecha actual, tendra 0 intentos y sera !valid
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        phoneValidation.setRequestData(formattedDate);

        phoneValidationRepository.save(phoneValidation);
        httpResponse.put("response", "Añadido registro de validación en la base de datos");

        return ResponseEntity.status(HttpStatus.CREATED).body(httpResponse);
    }


    /*
    recibe un objeto con la info necesaria para crear un nuevo usuario, si el codigo de
    verificacion de este objeto coincide con el codigo mas reciente guardado en la bd y
    ademas no se han empleado mas de 3 intentos en realizar la autentificacion se creara un '
    nuevo user
     */
    @Transactional
    @PostMapping(path = "/validatePhone")
    public ResponseEntity<?>validatePhone(@Valid @RequestBody CreateUserDTO createUserDTO){
        Map<String, Object> httpResponse = new HashMap<>();
        PhoneValidation phoneValidation = phoneValidationRepository.findPhoneValidationByPhone(createUserDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de servidor, vuelve a intentar el registro"));

        if (phoneValidation.getVerificationCode().equals(createUserDTO.getVerificationCode())  ) {
            phoneValidationRepository.setPhoneValidationTrue(createUserDTO.getUsername());
            createUser(createUserDTO);
            httpResponse.put("response", "Bienvenido, registro completado con exito");

            return ResponseEntity.status(HttpStatus.CREATED).body(httpResponse);
        }
        phoneValidationRepository.increaseAttempts(createUserDTO.getUsername());
        if (phoneValidation.getAttempts() >= 2) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(null);

    }


    /*
    funcion que se encarga de guardar un usuario en la bd despues de que este haya demostrado
    tras la verificacion de telefono que es el propietario de este
     */
    private UserEntity createUser(CreateUserDTO createUserDTO){
        Set<RoleEntity> roles = new HashSet<RoleEntity>();
        RoleEntity userRole = RoleEntity.builder()
                                        .name(ERole.valueOf("USER"))
                                        .build();
        roles.add(userRole);

        UserEntity newUser = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .roles(roles)
                .build();

        userRepository.save(newUser);
        return newUser;
    }

    @DeleteMapping(path = "/deleteUser")
    public void deleteUser(@RequestParam String id){
        userRepository.deleteById(Integer.parseInt(id));
    }
}
