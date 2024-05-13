package com.example.jwtacces.service;

import com.example.jwtacces.DTO.user.CreateUserDTO;
import com.example.jwtacces.models.userEntity.ERole;
import com.example.jwtacces.models.userEntity.RoleEntity;
import com.example.jwtacces.models.userEntity.UserEntity;
import com.example.jwtacces.models.registration.PhoneValidation;
import com.example.jwtacces.repository.UserRepository;
import com.example.jwtacces.repository.registration.PhoneValidationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AccessService {
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
    public ResponseEntity<?> confirmPhone(@Valid @RequestBody PhoneValidation phoneValidation) {
        Map<String, Object> httpResponse = new HashMap<>();
        Optional<PhoneValidation> phoneValidationOptional = phoneValidationRepository.findPhoneValidationByPhone(phoneValidation.getUsername());

        if (phoneValidationOptional.isPresent() && phoneValidationOptional.get().isValid()) {
            httpResponse.put("error","El numero ya esta registrado en la base de datos");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(httpResponse);
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
    public ResponseEntity<?>validatePhone(@Valid @RequestBody CreateUserDTO createUserDTO){
        Map<String, Object> httpResponse = new HashMap<>();
        PhoneValidation phoneValidation = phoneValidationRepository.findPhoneValidationByPhone(createUserDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de servidor, vuelve a intentar el registro"));

        if (phoneValidation.isValid()) {
            httpResponse.put("error", "Ya existe un usuario con ese numero de telefono");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(httpResponse);
        }
        if (phoneValidation.getAttempts() >= 3) {
            httpResponse.put("error", "Has superado el numero de intentos permitidos");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(httpResponse);
        }
        if (phoneValidation.getVerificationCode().equals(createUserDTO.getVerificationCode())  ) {
            phoneValidationRepository.setPhoneValidationTrue(createUserDTO.getUsername());
            createUser(createUserDTO);
            httpResponse.put("response", "Bienvenido, registro completado con exito");
            return ResponseEntity.status(HttpStatus.CREATED).body(httpResponse);
        }
        phoneValidationRepository.increaseAttempts(createUserDTO.getUsername());
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

    public void deleteUser(@RequestParam String id){
        userRepository.deleteById(Integer.parseInt(id));
    }
}
