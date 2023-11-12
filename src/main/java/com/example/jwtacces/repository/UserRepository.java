package com.example.jwtacces.repository;

import com.example.jwtacces.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    @Query("SELECT u FROM UserEntity u WHERE u.username like ?1")
    Optional<UserEntity>findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.phone like ?1")
    Optional<UserEntity>findByPhone(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email like ?1")
    Optional<UserEntity>findByEmail(String email);
}
