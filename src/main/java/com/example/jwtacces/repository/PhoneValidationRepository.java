package com.example.jwtacces.repository;

import com.example.jwtacces.models.PhoneValidation;
import com.example.jwtacces.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneValidationRepository extends JpaRepository<PhoneValidation, Integer> {
    @Query("SELECT p FROM PhoneValidation p " +
            "WHERE p.phone LIKE ?1 " +
            "AND p.requestData = (SELECT MAX(p2.requestData) FROM PhoneValidation p2 WHERE p2.phone LIKE ?1)")
    Optional<PhoneValidation> findPhoneValidationByPhone(String phone);

    @Modifying
    @Query("UPDATE PhoneValidation p SET p.valid = true WHERE p.phone LIKE ?1")
    int setPhoneValidationTrue(String phone);


}
