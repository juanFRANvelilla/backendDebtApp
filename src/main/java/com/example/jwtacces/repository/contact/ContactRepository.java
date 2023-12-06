package com.example.jwtacces.repository.contact;

import com.example.jwtacces.models.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {
    @Query("SELECT c.user2Id FROM Contact c WHERE c.userId = :userId")
    Optional<Set<Integer>> findContactIdsByUserId(@Param("userId") Long userId);
    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.userId = :userId AND c.user2Id = :user2Id")
    boolean isAlreadyContact(@Param("userId") Long userId, @Param("user2Id") Long user2Id);


}
