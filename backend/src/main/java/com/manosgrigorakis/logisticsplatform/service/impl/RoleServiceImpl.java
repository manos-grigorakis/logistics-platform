package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.role.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.role.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.mapper.RoleMapper;
import com.manosgrigorakis.logisticsplatform.model.Role;
import com.manosgrigorakis.logisticsplatform.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        Optional<Role> existingRole = roleRepository.findByName(dto.getName());

        if (existingRole.isPresent()) {
            throw new RuntimeException("Duplicate entry");
        }

        Role role = Role.builder()
                .name(dto.getName())
                .build();

        roleRepository.save(role);

        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        role.setName(dto.getName());

        Role updatedRole = roleRepository.save(role);

        return RoleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRoleById(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role not found with id: " + id);
        }

        roleRepository.deleteById(id);
    }
}
