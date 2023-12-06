package com.example.jwtacces.repository;

import com.example.jwtacces.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    @Query("SELECT u FROM UserEntity u WHERE u.username like :username")
    Optional<UserEntity>findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.phone like :phone")
    Optional<UserEntity> findByPhone(@Param("phone") String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.email like :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

}
