package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.common.exception.DeleteConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.users.mapper.RoleMapper;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
                .orElseThrow(() -> {
                        log.error("Role not found with id: {}", id);
                        return new ResourceNotFoundException("Role not found with id: " + id);
                });

        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        Optional<Role> existingRole = roleRepository.findByName(dto.getName());

        if (existingRole.isPresent()) {
            log.warn("Attempted to create duplicate role: {}", dto.getName());
            throw new DuplicateEntryException("name", dto.getName());
        }

        Role role = Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        roleRepository.save(role);
        log.info("Role created: {}", dto.getName());

        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. Role not found: {}", id);
                    return new ResourceNotFoundException("Role not found with id: " + id);
                });

        if(!role.isEditable()) {
            log.warn("Update failed. Attempt to edit non-editable role: {}", dto.getName());
            throw new AccessDeniedException("This role is protected and cannot be edited");
        }

        if(roleRepository.existsByNameAndIdNot(dto.getName(), id)) {
            log.warn("Update failed. Role name already exists: {}", dto.getName());
            throw new DuplicateEntryException("name", dto.getName());
        }

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated: {}", dto.getName());

        return RoleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRoleById(Long id) {
        if (!roleRepository.existsById(id)) {
            log.error("Delete failed. Role not found with id: {}", id);
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }

        long usersCount = userRepository.countByRoleId(id);

        if(usersCount > 0) {
            log.warn("Cannot delete role with id: {}. Active user(s) are still assigned", id);
            throw new DeleteConflictException("role", "user(s)");
        }

        roleRepository.deleteById(id);
        log.info("Role deleted: {}", id);
    }
}
