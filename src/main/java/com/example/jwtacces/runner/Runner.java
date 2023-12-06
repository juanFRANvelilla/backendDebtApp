//package com.example.jwtacces.runner;
//
//import com.example.jwtacces.models.ERole;
//import com.example.jwtacces.models.RoleEntity;
//import com.example.jwtacces.models.UserEntity;
//import com.example.jwtacces.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//
//@Component
//public class Runner implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        UserEntity user = UserEntity.builder()
//                .email("juanfran@gmail.com")
//                .phone("637650089")
//                .username("juanfran")
//                .password(passwordEncoder.encode("juanfran"))
//                .roles(Set.of(RoleEntity.builder()
//                        .name(ERole.ADMIN).build()))
//                .build();
//        userRepository.save(user);
//    }
//}
