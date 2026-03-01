package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
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

import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, AuditService auditService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
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

        Role role = RoleMapper.toEntity(dto);

        roleRepository.save(role);
        log.info("Role created: {}", dto.getName());
        this.logRole(role, AuditAction.CREATE);
        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. Role not found: {}", id);
                    return new ResourceNotFoundException("Role not found with id: " + id);
                });

        Role oldRole = new Role(role);

        if(!role.isEditable()) {
            log.warn("Update failed. Attempt to edit non-editable role: {}", dto.getName());
            throw new AccessDeniedException("This role is protected and cannot be edited");
        }

        if(roleRepository.existsByNameAndIdNot(dto.getName(), id)) {
            log.warn("Update failed. Role name already exists: {}", dto.getName());
            throw new DuplicateEntryException("name", dto.getName());
        }

        role.setName(dto.getName().toUpperCase());
        role.setDescription(dto.getDescription());

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated: {}", dto.getName());
        this.logUpdatedRole(oldRole, updatedRole);
        return RoleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRoleById(Long id) {
        Role role = this.roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Delete failed. Role not found with id: {}", id);
                    return  new ResourceNotFoundException("Role not found with id: " + id);
                });

        long usersCount = userRepository.countByRoleId(id);

        if(usersCount > 0) {
            log.warn("Cannot delete role with id: {}. Active user(s) are still assigned", id);
            throw new DeleteConflictException("role", "user(s)");
        }

        roleRepository.deleteById(id);
        log.info("Role deleted: {}", id);
        this.logRole(role, AuditAction.DELETE);

    }

    /**
     * Logs role in the audit system
     * @param role The role
     * @param action The action taken {@link AuditAction}
     */
    private void logRole(Role role, AuditAction action) {
        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Role")
                        .entityId(role.getId())
                        .action(action)
                        .notes("Name: " + role.getName())
                        .build()
        );
    }

    /**
     * Logs the updated role in the audit system, with only the updated changes
     * @param oldRole Old values of role before updating
     * @param updatedRole New values of role after updating
     */
    private void logUpdatedRole(Role oldRole, Role updatedRole) {
        Map<String, Object> changes =  new HashMap<>();

        if(!Objects.equals(oldRole.getName(), updatedRole.getName())) {
            changes.put("name", Map.of(
                    "old:" + oldRole.getName(),
                    "new:" + updatedRole.getName()
            ));
        }

        if(!Objects.equals(oldRole.getDescription(), updatedRole.getDescription())) {
            changes.put("description", Map.of(
                    "old:" + oldRole.getDescription(),
                    "new:" + updatedRole.getDescription()
            ));
        }

        if(changes.isEmpty()) return;

        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Role")
                        .entityId(updatedRole.getId())
                        .changes(changes)
                        .action(AuditAction.UPDATE)
                        .build()
        );
    }
}
