package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.role.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.role.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();

    RoleResponseDTO getRoleById(Long id);

    RoleResponseDTO createRole(RoleRequestDTO dto);

    RoleResponseDTO updateRole(Long id, RoleRequestDTO dto);

    void deleteRoleById(Long id);
}
