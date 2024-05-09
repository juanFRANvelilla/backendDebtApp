package com.example.jwtacces.service.utils;

import com.example.jwtacces.DTO.UserDTO;
import com.example.jwtacces.DTO.UsernameDTO;
import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ServiceUtils {
    @Autowired
    private UserRepository userRepository;



    /* obtiene un set de usuarios buscandolos por el id */
    public Set<UserDTO> getUsersById(Set<Integer> contactId) throws UsernameNotFoundException {
        Set<UserDTO>contacts = new HashSet<UserDTO>();
        for(Integer id : contactId){
            UserEntity contact = userRepository.findById(id)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            UserDTO userDTO = UserDTO.builder()
                    .username(contact.getUsername())
                    .firstName(contact.getFirstName())
                    .lastName(contact.getLastName())
                    .email(contact.getEmail())
                    .build();
            contacts.add(userDTO);
        }
        return contacts;
    }

    /* obtienes al usuario que ha realizado la peticion con un jwt */
    public UserEntity getUserFromAuthentification(Authentication authentication){
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return user;
    }

    /* retorna el user que se corresponde a la peticion de amistad */
    public UserEntity getUserFromUsernameDTO(UsernameDTO usernameDTO){
        String usernameContact = usernameDTO.getUsername();
        UserEntity contact = null;
        contact = userRepository.findByUsername(usernameContact)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return contact;
    }
}
