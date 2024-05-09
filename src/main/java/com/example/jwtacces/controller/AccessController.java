package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.CreateUserDTO;
import com.example.jwtacces.models.*;
import com.example.jwtacces.models.registration.PhoneValidation;
import com.example.jwtacces.service.AccessService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(path = "/api")
public class AccessController {

    @Autowired
    private AccessService accessService;

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
        return accessService.confirmPhone(phoneValidation);
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
        return accessService.validatePhone(createUserDTO);
    }




    @DeleteMapping(path = "/deleteUser")
    public void deleteUser(@RequestParam String id){
        accessService.deleteUser(id);
    }
}
