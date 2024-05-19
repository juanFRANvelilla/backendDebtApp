package com.example.jwtacces.service;

import com.example.jwtacces.DTO.notification.DebtNotificationDTO;
import com.example.jwtacces.models.debt.Debt;
import com.example.jwtacces.models.notification.DebtNotification;
import com.example.jwtacces.models.userEntity.UserEntity;
import com.example.jwtacces.repository.UserRepository;
import com.example.jwtacces.repository.debt.DebtRepository;
import com.example.jwtacces.repository.notification.NotificationRepository;
import com.example.jwtacces.service.utils.ServiceUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DebtRepository debtRepository;

    /*
    del backend obtienes un objeto dto con info sobre la deuda, el usuario que debe de pagarla y creas el objeto notificacion
     */
    public ResponseEntity<?> createDebtNotification(@Valid DebtNotificationDTO debtNotificationDTO) {
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //obtener la deuda en bd a la que hace referencia la debtDTO
        Debt debt = debtRepository.findById(Math.toIntExact(debtNotificationDTO.getDebt().getId())).orElseThrow(() -> new UsernameNotFoundException("Debt not found"));

        DebtNotification debtNotification = DebtNotification.builder()
                .debt(debt)
                .date(java.time.LocalDateTime.now())

                .build();

        notificationRepository.save(debtNotification);
        httpResponse.put("response","Notificacion de deuda creada correctamente");
        return ResponseEntity.ok(httpResponse);
    }


    public List<DebtNotificationDTO> getDebtNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = serviceUtils.getUserFromAuthentification(authentication);
        List<DebtNotification> debtNotifications = notificationRepository.findAllByUserId(Long.valueOf(user.getId()));
        List<DebtNotificationDTO> debtNotificationDTOS = new ArrayList<>();
        //convertir las notificaciones de deuda a DTO
        for (DebtNotification debtNotification : debtNotifications) {
            DebtNotificationDTO debtNotificationDTO = DebtNotificationDTO.builder()
                    .debt(serviceUtils.convertDebtToDTO(debtNotification.getDebt(), debtNotification.getDebt().getCreditor()))
                    .date(debtNotification.getDate().toString())
                    .build();
            debtNotificationDTOS.add(debtNotificationDTO);
        }
        return debtNotificationDTOS;
    }
}
