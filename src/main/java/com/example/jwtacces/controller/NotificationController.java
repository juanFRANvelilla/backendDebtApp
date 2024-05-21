package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.notification.DebtNotificationDTO;
import com.example.jwtacces.DTO.notification.NotificationDTO;
import com.example.jwtacces.DTO.requestContact.RequestContactDTO;
import com.example.jwtacces.service.ContactService;
import com.example.jwtacces.service.NotificationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/notification")
public class NotificationController {
    @Autowired
    private ContactService contactService;

    @Autowired
    private NotificationService notificationService;



    /*
    devuelve las notificaciones
     */
    @GetMapping(path = "/getNotifications")
    public ResponseEntity<?> showRequestContact(){
        //obtener las peticiones de contacto lista que contiene (userDTO que hace la peticion, fecha de la peticion)
        List<RequestContactDTO> requestContact = contactService.showRequestContact();
        //obtener las notificaciones de deuda
        List<DebtNotificationDTO> debtNotifications = notificationService.getDebtNotifications();

        //crea un objeto de notificaciones y se las asigna
        NotificationDTO requestContactsDTO = NotificationDTO.builder()
                .requestContactList(requestContact)
                .debtNotificationList(debtNotifications)
                .build();

        return ResponseEntity.ok().body(requestContactsDTO);
    }

    /*
    crea una notificacion de deuda
     */
    @PostMapping(path = "/createDebtNotification")
    public ResponseEntity<?> createDebtNotification(@Valid @RequestBody DebtNotificationDTO debtNotification){
        return notificationService.createDebtNotification(debtNotification);
    }

    /*
    elimina una notificacion de deuda por id de deuda y fecha de creacion
     */
    @Transactional
    @PostMapping(path = "/deleteDebtNotification")
    public ResponseEntity<?> deleteDebtNotification(@Valid @RequestBody DebtNotificationDTO debtNotification){
        return notificationService.deleteDebtNotification(debtNotification);
    }

}
