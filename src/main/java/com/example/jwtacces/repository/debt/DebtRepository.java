package com.example.jwtacces.repository.debt;

import com.example.jwtacces.models.debt.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DebtRepository extends JpaRepository<Debt,Integer> {
    @Query("SELECT d FROM Debt d WHERE d.creditorId = :creditorId AND d.debtorId = :debtorId order by d.date desc")
    Optional<List<Debt>>getDebtByCreditorAndDebtor(Long creditorId, Long debtorId);

    @Query("SELECT d FROM Debt d WHERE d.creditorId = :creditorId AND d.debtorId = :debtorId AND d.isPaid = false order by d.date desc")
    Optional<List<Debt>>getDebtByCreditorAndDebtorNotPaid(Long creditorId, Long debtorId);
}
