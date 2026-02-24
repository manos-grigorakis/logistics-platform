package com.manosgrigorakis.logisticsplatform.users.mapper;

import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;

public class UserMapper {
    // DTO => Entity
    public static User toEntity(UserRequestDTO dto, Role role) {
        String phone = null;

        if (dto.getPhone() != null && dto.getPhone().isBlank()) {
            phone = dto.getPhone();
        }

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(phone)
                .build();

        user.setRole(role);

        return user;
    }

    // Entity => Response
    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getName());
        }

        return dto;
    }
}
