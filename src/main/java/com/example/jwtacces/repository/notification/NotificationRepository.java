package com.example.jwtacces.repository.notification;

import com.example.jwtacces.models.notification.DebtNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<DebtNotification,Integer> {
    //obtener las notificaciones de deuda por el id del deudor
    @Query("SELECT d FROM DebtNotification d WHERE d.debt.debtor.id = ?1")
    List<DebtNotification> findAllByUserId(Long userId);
}
