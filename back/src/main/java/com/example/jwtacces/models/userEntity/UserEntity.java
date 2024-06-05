package com.example.jwtacces.models.userEntity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @SequenceGenerator(name = "idUsers",
            sequenceName = "idUsers",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idUsers")
    private Integer id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER,
            targetEntity = RoleEntity.class,
            cascade = CascadeType.PERSIST)

    @JoinTable(name =  "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;



}
