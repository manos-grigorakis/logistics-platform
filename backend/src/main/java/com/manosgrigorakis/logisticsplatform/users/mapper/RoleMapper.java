package com.manosgrigorakis.logisticsplatform.users.mapper;

import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", expression = "java(dto.getName().toUpperCase())")
    Role toEntity(RoleRequestDTO dto);

    RoleResponseDTO toResponse(Role entity);
}
