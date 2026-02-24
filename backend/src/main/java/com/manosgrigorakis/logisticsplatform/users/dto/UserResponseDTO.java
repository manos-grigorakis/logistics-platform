package com.manosgrigorakis.logisticsplatform.users.dto;

import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
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
    private String roleName;
    private UserStatus status;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
