package com.example.jwtacces.repository.registration;

import com.example.jwtacces.models.registration.PhoneValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneValidationRepository extends JpaRepository<PhoneValidation, Integer> {
    @Query("SELECT p FROM PhoneValidation p " +
            "WHERE p.username LIKE :username " +
            "AND p.requestData = (SELECT MAX(p2.requestData) FROM PhoneValidation p2 WHERE p2.username LIKE :username)")
    Optional<PhoneValidation> findPhoneValidationByPhone(@Param("username") String username);

    @Modifying
    @Query("UPDATE PhoneValidation p " +
            "SET p.valid = true " +
            "WHERE p.username LIKE :username AND p.requestData = " +
            "(SELECT MAX(p2.requestData) FROM PhoneValidation p2 WHERE p2.username LIKE :username)")
    int setPhoneValidationTrue(@Param("username") String username);


    @Modifying
    @Query("UPDATE PhoneValidation p " +
            "SET p.attempts = p.attempts + 1 " +
            "WHERE p.username LIKE :username AND p.requestData = " +
            "(SELECT MAX(p2.requestData) FROM PhoneValidation p2 WHERE p2.username LIKE :username)")
    int increaseAttempts(@Param("username") String username);


}
