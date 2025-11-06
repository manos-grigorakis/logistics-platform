package com.manosgrigorakis.logisticsplatform.mapper;

import com.manosgrigorakis.logisticsplatform.dto.role.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.role.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.model.Role;

public class RoleMapper {
    // DTO => Entity
    public static Role toEntity(RoleRequestDTO dto) {
        return Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    // Entity => Response
    public static RoleResponseDTO toResponse(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());

        return dto;
    }

}
