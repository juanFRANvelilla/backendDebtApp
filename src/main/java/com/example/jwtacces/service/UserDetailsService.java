package com.example.jwtacces.service;

import com.example.jwtacces.models.ERole;
import com.example.jwtacces.models.RoleEntity;
import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.registration.RegistrationRequest;
import com.example.jwtacces.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found."));

        List< GrantedAuthority> authorities = new ArrayList<>();
        for(RoleEntity role : userEntity.getRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())));
        }

        return new User(userEntity.getUsername(), userEntity.getPassword(),true,true,true,true,authorities);
    }

    public User registerUser(RegistrationRequest request){
        return null;
    }
}
