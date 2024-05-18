package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.notification.NotificationDTO;
import com.example.jwtacces.DTO.requestContact.RequestContactDTO;
import com.example.jwtacces.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/notification")
public class NotificationController {
    @Autowired
    private ContactService contactService;

    /*
    devuelve las notificaciones
     */
    @GetMapping(path = "/getNotifications")
    public ResponseEntity<?> showRequestContact(){
        //obtener las peticiones de contacto lista que contiene (userDTO que hace la peticion, fecha de la peticion)
        List<RequestContactDTO> requestContact = contactService.showRequestContact();

        //crea un objeto de notificaciones y se las asigna
        NotificationDTO requestContactsDTO = NotificationDTO.builder()
                .requestContactList(requestContact)
                .build();

        return ResponseEntity.ok().body(requestContactsDTO);
    }

}
