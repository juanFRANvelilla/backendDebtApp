package com.example.jwtacces.service;

import com.example.jwtacces.DTO.requestContact.RequestContactDTO;
import com.example.jwtacces.DTO.user.UserDTO;
import com.example.jwtacces.models.userEntity.UserEntity;
import com.example.jwtacces.models.contact.Contact;
import com.example.jwtacces.models.contact.RequestContact;
import com.example.jwtacces.repository.UserRepository;
import com.example.jwtacces.repository.contact.ContactRepository;
import com.example.jwtacces.repository.contact.ContactRequestRepository;
import com.example.jwtacces.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactRequestRepository contactRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceUtils serviceUtils;



    public String welcome() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);
        String welcome = "Hola, " + user.getFirstName() + " tu telefono es: " + user.getUsername();
        return welcome;
    }

    public ResponseEntity<?> showContacts(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);
        Set<Integer>contactsId = new HashSet<Integer>();
        Set<UserDTO>contacts;
        try {
            //busca los id en la tabla contactos, solo se obtiene de info el id
            contactsId = contactRepository.findContactIdsByUserId(Long.valueOf(user.getId()))
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));

            //busca info de los contactos haciendo uso de los anteriores id
            contacts = serviceUtils.getUsersById(contactsId);

        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(contacts);
    }




    public ResponseEntity<?> doRequestContact(String usernameDTO){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);
        UserEntity contact = new UserEntity();
        try{
            contact = serviceUtils.getUserFromUsername(usernameDTO);
        } catch (UsernameNotFoundException e) {
            httpResponse.put("error","No puedes mandar solicitud a ese numero");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        if(contact == null || user.getId() == contact.getId()){
            httpResponse.put("error","No puedes mandar solicitud a ese numero");
            return ResponseEntity.badRequest().body(httpResponse);
        }
        if (contactRepository.isAlreadyContact(Long.valueOf(user.getId()), Long.valueOf(contact.getId()))) {
            httpResponse.put("error","Ya tienes a ese usuario como contacto");
            return ResponseEntity.badRequest().body(httpResponse);
        }
        else{
            if(!contactRequestRepository.requestAlreadyExist(Long.valueOf(contact.getId()), Long.valueOf(user.getId()))){
                RequestContact requestContact = RequestContact.builder()
                        .date(java.time.LocalDateTime.now())
                        .userId(Long.valueOf(contact.getId()))
                        .userRequestId(Long.valueOf(user.getId()))
                        .accept(false)
                        .build();
                contactRequestRepository.save(requestContact);
                httpResponse.put("response","Solicitud de contacto enviada con éxito");
                return ResponseEntity.ok(httpResponse);
            } else {
                httpResponse.put("error","Ya has enviado una solicitud a esa persona anteriormente");
                return ResponseEntity.badRequest().body(httpResponse);
            }
        }
    }

    public List<RequestContactDTO> showRequestContact(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);

        List<RequestContactDTO> requestContactsDTO = new ArrayList<>();
        Set<RequestContact> requestContacts = new HashSet<>();
        try {
            //buscar los request que tiene el usuario
            requestContacts = contactRequestRepository.findRequestContactByUserId(Long.valueOf(user.getId()))
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));

            //recorre los request y busca el usuario que lo envio a traves del id
            for(RequestContact requestContact : requestContacts){
                UserEntity userDoRequest = userRepository.findById(Math.toIntExact(requestContact.getUserRequestId()))
                        .orElseThrow(()-> new UsernameNotFoundException("User not found"));
                UserDTO userDoRequestDTO = UserDTO.builder()
                        .username(userDoRequest.getUsername())
                        .firstName(userDoRequest.getFirstName())
                        .lastName(userDoRequest.getLastName())
                        .email(userDoRequest.getEmail())
                        .build();

                RequestContactDTO requestContactDTO = RequestContactDTO.builder()
                        .userRequest(userDoRequestDTO)
                        .date(requestContact.getDate().toString())
                        .build();

                requestContactsDTO.add(requestContactDTO);

            }
        } catch (EmptyResultDataAccessException e) {
        } catch (UsernameNotFoundException e) {
        }
        return requestContactsDTO;
    }


    public ResponseEntity<?> acceptRequestContact(String usernameDTO){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);
        UserEntity contact = serviceUtils.getUserFromUsername(usernameDTO);
        //comprueba que el user que se manda como request no sea nulo ni sea el mismo usuario
        if(contact == null || user.getId() == contact.getId()){
            httpResponse.put("error","Error en el request");
            return ResponseEntity.badRequest().body(httpResponse);
        }
        //comprueba que no sean ya contactos
        if (contactRepository.isAlreadyContact(Long.valueOf(user.getId()), Long.valueOf(contact.getId()))) {
            httpResponse.put("error","Ya son contactos");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        //actualiza las filas de la tabla en la bd aceptando la solicitud y tambien la posible solicitud que tenga el otro contacto
        //para que no se queden solicitudes pendientes
        int requestAccepted = 0;
        requestAccepted += contactRequestRepository.validateRequest(Long.valueOf(user.getId()), Long.valueOf(contact.getId()));
        requestAccepted += contactRequestRepository.validateRequest(Long.valueOf(contact.getId()), Long.valueOf(user.getId()));
        if(requestAccepted > 0){
            Contact addContact = Contact.builder()
                    .userId(Long.valueOf(user.getId()))
                    .user2Id(Long.valueOf(contact.getId()))
                    .build();
            Contact addContactInverse = Contact.builder()
                    .userId(Long.valueOf(contact.getId()))
                    .user2Id(Long.valueOf(user.getId()))
                    .build();
            contactRepository.save(addContact);
            contactRepository.save(addContactInverse);
            httpResponse.put("response","Contacto agregado");
            return ResponseEntity.ok(httpResponse);
        }
        httpResponse.put("response","Error al agregar el contacto");
        return ResponseEntity.badRequest().body(httpResponse);
    }
}
