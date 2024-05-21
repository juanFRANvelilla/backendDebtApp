package com.example.jwtacces.controller;

import com.example.jwtacces.service.ContactService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/contact")
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
    @GetMapping(path = "/requestContact")
    public ResponseEntity<?> doRequestContact(@RequestParam String contactUsername){
        return contactService.doRequestContact(contactUsername);
    }


    /*
    funcion que se encarga de aceptar la solicitud de contacto y marcar como aceptadas en true las filas involucradas
    si por ejemplo dados dos usuarios x e y, x tiene solicitud de contacto de y (y viceversa) ambos pasaran a ser contactos
    y las dos solicitudes de contacto estaran aceptadas y ya no apareceran como notificaciones
     */
    @Transactional
    @GetMapping(path = "/acceptRequestContact")
    public ResponseEntity<?> acceptRequestContact(@RequestParam String contactUsername){
        return contactService.acceptRequestContact(contactUsername);
    }

}
