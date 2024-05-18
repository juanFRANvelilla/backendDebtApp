package com.example.jwtacces.DTO.requestContact;

import com.example.jwtacces.DTO.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RequestContactDTO {
    private UserDTO userRequest;
    private LocalDateTime date;
}
