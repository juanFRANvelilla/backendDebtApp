package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.UsernameDTO;
import com.example.jwtacces.DTO.UserDTO;
import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.models.contact.Contact;
import com.example.jwtacces.models.contact.RequestContact;
import com.example.jwtacces.repository.UserRepository;
import com.example.jwtacces.repository.contact.ContactRepository;
import com.example.jwtacces.repository.contact.ContactRequestRepository;
import com.example.jwtacces.service.ContactService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api2")
public class ContactsController {
    @Autowired
    private ContactService contactService;




    @GetMapping(path = "/welcome")
    public String welcome() {
        return contactService.welcome();
    }

    /* devuelve los contactos que tiene cada usuario, si no tiene devolvera null */
    @GetMapping(path = "/showContacts")
    public ResponseEntity<?> showContacts(){
        return contactService.showContacts();
    }




    /*
    realiza la logica que se encarga de mandar una soliticud a otro usuario para ser contactos, siempre y cuando
    este usuario exista, no seas tu mismo, ni haya una solicitud pendiente
     */
    @PostMapping(path = "/requestContact")
    public ResponseEntity<?> doRequestContact(@Valid @RequestBody UsernameDTO usernameDTO){
        return contactService.doRequestContact(usernameDTO);
    }

    /*
    devuelve la lista de usuarios que tienes pendientes de aceptar
     */
    @GetMapping(path = "/showRequestContact")
    public ResponseEntity<?> showRequestContact(){
        return contactService.showRequestContact();
    }


    /*
    funcion que se encarga de aceptar la solicitud de contacto y marcar como aceptadas en true las filas involucradas
    si por ejemplo dados dos usuarios x e y, x tiene solicitud de contacto de y (y viceversa) ambos pasaran a ser contactos
    y las dos solicitudes de contacto estaran aceptadas y ya no apareceran como notificaciones
     */
    @Transactional
    @PostMapping(path = "/acceptRequestContact")
    public ResponseEntity<?> acceptRequestContact(@Valid @RequestBody UsernameDTO usernameDTO){
        return contactService.acceptRequestContact(usernameDTO);
    }
}
