package com.manosgrigorakis.logisticsplatform.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
