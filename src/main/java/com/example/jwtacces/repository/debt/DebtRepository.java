package com.example.jwtacces.repository.debt;

import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.models.debt.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt,Integer> {
    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.debtor = :debtor order by d.date desc")
    List<Debt>getDebtByCreditorAndDebtor(UserEntity creditor, UserEntity debtor);

    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.isPaid = false")
    List<Debt>getDebtByCreditorNotPaid(UserEntity creditor);

    @Query("SELECT d FROM Debt d WHERE d.debtor = :debtor AND d.isPaid = false")
    List<Debt>getDebtByDebtorNotPaid(UserEntity debtor);

    @Query("SELECT d.creditor FROM Debt d WHERE d.id = :debtId AND d.isPaid = false")
    Optional<UserEntity>getCreditorOfDebt(Long debtId);

    @Modifying
    @Query("UPDATE Debt d SET d.isPaid = true WHERE d.id = :debtId")
    int setDebtPaid(Long debtId);
}
