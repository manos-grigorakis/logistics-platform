package com.manosgrigorakis.logisticsplatform.mapper;

import com.manosgrigorakis.logisticsplatform.dto.role.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.model.Role;

public class RoleMapper {
    // DTO => Entity
    public static Role toEntity(RoleRequestDTO dto) {
        return Role.builder()
                .name(dto.getName())
                .build();
    }

    // Entity => Response
    public static RoleRequestDTO toResponse(Role role) {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName(role.getName());

        return dto;
    }

}
