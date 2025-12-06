package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();

    RoleResponseDTO getRoleById(Long id);

    RoleResponseDTO createRole(RoleRequestDTO dto);

    RoleResponseDTO updateRole(Long id, RoleRequestDTO dto);

    void deleteRoleById(Long id);
}
