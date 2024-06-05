package com.example.jwtacces.repository.notification;

import com.example.jwtacces.models.notification.DebtNotification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<DebtNotification,Integer> {
    //obtener las notificaciones de deuda por el id del deudor
    @Query("SELECT d FROM DebtNotification d WHERE d.debt.debtor.id = ?1")
    List<DebtNotification> findAllByUserId(Long userId);

    //eliminar una notificacion de deuda concreta al aceptarla
    @Transactional
    @Modifying
    @Query("DELETE FROM DebtNotification d WHERE d.debt.id = :debtId AND d.date = :date")
    void deleteByDebtIdAndDate(Long debtId, LocalDateTime date);

    //eliminar todas las notificaciones asociadas a una deuda
    @Transactional
    @Modifying
    @Query("DELETE FROM DebtNotification d WHERE d.debt.id = :debtId")
    void deleteByDebtId(Long debtId);
}
