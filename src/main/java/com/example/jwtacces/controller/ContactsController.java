package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.RequestContactDTO;
import com.example.jwtacces.exceptions.NoContactsException;
import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.models.contact.Contact;
import com.example.jwtacces.models.contact.RequestContact;
import com.example.jwtacces.repository.UserRepository;
import com.example.jwtacces.repository.contact.ContactRepository;
import com.example.jwtacces.repository.contact.ContactRequestRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(path = "/api2")
public class ContactsController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactRequestRepository contactRequestRepository;


    public UserEntity getUserFromAuthentification(Authentication authentication){
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return user;

    }

    public UserEntity getUserFromRequestContactDTO(RequestContactDTO requestContactDTO){
        String usernameContact = requestContactDTO.getUsername();
        String phoneContact = requestContactDTO.getPhone();
        UserEntity contact = null;
        if(usernameContact != ""){
            contact = userRepository.findByUsername(usernameContact)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        }
        else if(phoneContact != ""){
            contact = userRepository.findByPhone(phoneContact)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        }
        return contact;

    }

    @GetMapping(path = "/welcome")
    public String welcome() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = getUserFromAuthentification(authentication);
        String welcome = "Hola, " + user.getUsername() + " tu telefono es: " + user.getPhone();
        return welcome;
    }

    @GetMapping(path = "/showContact")
    public Set<String> showContact(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = getUserFromAuthentification(authentication);
        Set<Integer>contactsId = new HashSet<Integer>();
        try {
            contactsId = contactRepository.findContactIdsByUserId(Long.valueOf(user.getId()))
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));
        } catch (EmptyResultDataAccessException e) {
            throw new NoContactsException("El usuario no tiene contactos", e);
        }
        Set<String>contactsUsername = new HashSet<String>();
        for(Integer id : contactsId){
            UserEntity contact = userRepository.findById(id)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            contactsUsername.add(contact.getUsername());
        }
        return contactsUsername;
    }

    @PostMapping(path = "/requestContact")
    public ResponseEntity<?> doRequestContact(@Valid @RequestBody RequestContactDTO requestContactDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = getUserFromAuthentification(authentication);
        UserEntity contact = getUserFromRequestContactDTO(requestContactDTO);
        if(contact == null || user.getId() == contact.getId()){
            return ResponseEntity.badRequest().body("Body del request invalido");
        }
        if (contactRepository.isAlreadyContact(Long.valueOf(user.getId()), Long.valueOf(contact.getId()))) {
            return ResponseEntity.badRequest().body("Ya son contactos");
        }
        else{
            if(!contactRequestRepository.requestAlreadyExist(Long.valueOf(contact.getId()), Long.valueOf(user.getId()))){
                RequestContact requestContact = RequestContact.builder()
                        .userId(Long.valueOf(contact.getId()))
                        .userRequestId(Long.valueOf(user.getId()))
                        .accept(false)
                        .build();
                contactRequestRepository.save(requestContact);
                return ResponseEntity.ok("Solicitud de contacto enviada con éxito");
            }
            return ResponseEntity.badRequest().body("Ya has enviado una solicitud a esa persona anteriormente");
        }
    }

    @GetMapping(path = "/showRequestContact")
    public Set<String> showRequestContact(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = getUserFromAuthentification(authentication);

        Set<Integer>contactRequestSendersId = new HashSet<Integer>();
        try {
            contactRequestSendersId = contactRequestRepository.findRequiestContactIdsByUserId(Long.valueOf(user.getId()))
                    .orElseThrow(() -> new EmptyResultDataAccessException(1));
        } catch (EmptyResultDataAccessException e) {
            throw new NoContactsException("El usuario no tiene peticiones de contacto", e);
        }
        Set<String>contactsUsername = new HashSet<String>();
        for(Integer id : contactRequestSendersId){
            UserEntity contact = userRepository.findById(id)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            contactsUsername.add(contact.getUsername());
        }
        return contactsUsername;
    }

    @Transactional
    @PostMapping(path = "/acceptRequestContact")
    public ResponseEntity<?> acceptRequestContact(@Valid @RequestBody RequestContactDTO requestContactDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = getUserFromAuthentification(authentication);
        UserEntity contact = getUserFromRequestContactDTO(requestContactDTO);
        if(contact == null || user.getId() == contact.getId()){
            return ResponseEntity.badRequest().body("Body del request invalido");
        }
        if (contactRepository.isAlreadyContact(Long.valueOf(user.getId()), Long.valueOf(contact.getId()))) {
            return ResponseEntity.badRequest().body("Ya son contactos");
        }
        if(contactRequestRepository.validateRequest(Long.valueOf(user.getId()), Long.valueOf(contact.getId())) > 0
            || contactRequestRepository.validateRequest(Long.valueOf(contact.getId()), Long.valueOf(user.getId())) > 0){
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
            return ResponseEntity.ok("Contacto agregado");
        }
        return ResponseEntity.badRequest().body("Error al agregar el contacto");
    }
}
