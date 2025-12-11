package com.manosgrigorakis.logisticsplatform.users.mapper;

import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.model.Role;

public class RoleMapper {
    // DTO => Entity
    public static Role toEntity(RoleRequestDTO dto) {
        return Role.builder()
                .name(dto.getName().toUpperCase())
                .description(dto.getDescription())
                .build();
    }

    // Entity => Response
    public static RoleResponseDTO toResponse(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setEditable(role.isEditable());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());

        return dto;
    }

}
