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
            "WHERE p.phone LIKE :phone " +
            "AND p.requestData = (SELECT MAX(p2.requestData) FROM PhoneValidation p2 WHERE p2.phone LIKE :phone)")
    Optional<PhoneValidation> findPhoneValidationByPhone(@Param("phone") String phone);

    @Modifying
    @Query("UPDATE PhoneValidation p SET p.valid = true WHERE p.phone LIKE :phone")
    int setPhoneValidationTrue(@Param("phone") String phone);
}
