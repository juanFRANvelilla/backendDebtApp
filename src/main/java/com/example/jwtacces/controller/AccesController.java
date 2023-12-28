package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.*;
import com.example.jwtacces.models.registration.PhoneValidation;
import com.example.jwtacces.repository.registration.PhoneValidationRepository;
import com.example.jwtacces.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class AccesController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneValidationRepository phoneValidationRepository;

    /*verifica que el telefono con el que se quiere registrar un nuevo usuario no este ya siendo
    utilizado en la app, se manda un codigo aleatorio creado en el front que se mandara por sms
    que luego se tendra que validar
     */
    @PostMapping(path = "/confirmPhone")
    public ResponseEntity<?> confirmPhone(@Valid @RequestBody PhoneValidation phoneValidation) {
        Optional<PhoneValidation> phoneValidationOptional = phoneValidationRepository.findPhoneValidationByPhone(phoneValidation.getUsername());

        if (phoneValidationOptional.isPresent() && phoneValidationOptional.get().isValid()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El número de teléfono ya está registrado y confirmado");
        }

        //agregamos un nuevo registro de validacion de telefono con la fecha actual, tendra 0 intentos y sera !valid
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        phoneValidation.setRequestData(formattedDate);

        phoneValidationRepository.save(phoneValidation);
        return ResponseEntity.status(HttpStatus.CREATED).body("Añadido registro de validacion en la base de datos");
    }


    /*recibe un objeto con la info necesaria para crear un nuevo usuario, si el codigo de
    verificacion de este objeto coincide con el codigo mas reciente guardado en la bd y
    ademas no se han empleado mas de 3 intentos en realizar la autentificacion se creara un '
    nuevo user
     */
    @Transactional
    @PostMapping(path = "/validatePhone")
    public ResponseEntity<?>validatePhone(@Valid @RequestBody CreateUserDTO createUserDTO){
        PhoneValidation phoneValidation = phoneValidationRepository.findPhoneValidationByPhone(createUserDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de servidor, vuelve a intentar el registro"));

        if (phoneValidation.getAttempts() >= 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A caducado tus intentos para introducir el codigo");
        }
        phoneValidationRepository.increaseAttempts(createUserDTO.getUsername());
        if (phoneValidation.getVerificationCode() == createUserDTO.getVerificationCode()) {
            phoneValidationRepository.setPhoneValidationTrue(createUserDTO.getUsername());
            createUser(createUserDTO);
            return ResponseEntity.ok("Bienvenido, registro completado con exito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Codigo no valido");

    }


    /*funcion que se encarga de guardar un usuario en la bd despues de que este haya demostrado
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
                .name(createUserDTO.getName())
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
