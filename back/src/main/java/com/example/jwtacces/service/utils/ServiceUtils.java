package com.example.jwtacces.service.utils;

import com.example.jwtacces.DTO.debt.DebtDTO;
import com.example.jwtacces.DTO.user.UserDTO;
import com.example.jwtacces.models.debt.Debt;
import com.example.jwtacces.models.userEntity.UserEntity;
import com.example.jwtacces.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

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

    /* retorna el user by username */
    public UserEntity getUserFromUsername(String username){
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return userEntity;
    }

    public static DebtDTO convertDebtToDTO(Debt debt, UserEntity user){
        boolean isUserCreditor = Objects.equals(debt.getCreditor().getUsername(), user.getUsername());
        UserEntity counterpartyUser;
        //determinamos si el usuario de la contrapartida es el acreedor o el deudor
        if(isUserCreditor){
            counterpartyUser = debt.getDebtor();
        } else {
            counterpartyUser = debt.getCreditor();
        }

        UserDTO counterpartyUserDTO = UserDTO.builder()
                .username(counterpartyUser.getUsername())
                .firstName(counterpartyUser.getFirstName())
                .lastName(counterpartyUser.getLastName())
                .email(counterpartyUser.getEmail())
                .build();

        DebtDTO debtDTO = DebtDTO.builder()
                .id(debt.getId())
                .isCreditor(isUserCreditor)
                .counterpartyUser(counterpartyUserDTO)
                .amount(debt.getAmount())
                .date(debt.getDate())
                .description(debt.getDescription())
                .isPaid(debt.getIsPaid())
                .build();
        return debtDTO;
    }
}
