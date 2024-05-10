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
    //obtener las deudas que tienes activas como acreedor
    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.isPaid = false")
    List<Debt>getCurrentDebtsAsCreditor(UserEntity creditor);

    //obtener las deudas que tienes activas como deudor
    @Query("SELECT d FROM Debt d WHERE d.debtor = :debtor AND d.isPaid = false")
    List<Debt>getCurrentDebtsAsDebtor(UserEntity debtor);

    //obtener deudas por acreedor y deudor esten saldadas o no (para historial)
    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.debtor = :debtor order by d.date desc")
    List<Debt>getDebtByCreditorAndDebtor(UserEntity creditor, UserEntity debtor);

    //obtener deudas por acreedor y deudor que no esten saldadas para modificarlas al agregar una nueva deuda
    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.debtor = :debtor AND d.isPaid = false order by d.date desc")
    List<Debt>getDebtByCreditorAndDebtorNotPaid(UserEntity creditor, UserEntity debtor);

    //obtener deudas como acreedor que no estan saldadas
    @Query("SELECT d FROM Debt d WHERE d.creditor = :creditor AND d.isPaid = false")
    List<Debt>getDebtByCreditorNotPaid(UserEntity creditor);

    //obtener deudas como deudor que no estan saldadas
    @Query("SELECT d FROM Debt d WHERE d.debtor = :debtor AND d.isPaid = false")
    List<Debt>getDebtByDebtorNotPaid(UserEntity debtor);

    //obtener el acreedor de una deuda por id de deuda
    @Query("SELECT d.creditor FROM Debt d WHERE d.id = :debtId AND d.isPaid = false")
    Optional<UserEntity>getCreditorOfDebt(Long debtId);


    //marcar deuda como pagada
    @Modifying
    @Query("UPDATE Debt d SET d.isPaid = true WHERE d.id = :debtId")
    int setDebtPaid(Long debtId);
}
