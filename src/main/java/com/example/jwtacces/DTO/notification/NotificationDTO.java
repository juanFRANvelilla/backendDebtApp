package com.example.jwtacces.DTO.notification;

import com.example.jwtacces.DTO.requestContact.RequestContactDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Builder
@Getter
@Setter
public class NotificationDTO {
    private List<RequestContactDTO> requestContactList;
}
