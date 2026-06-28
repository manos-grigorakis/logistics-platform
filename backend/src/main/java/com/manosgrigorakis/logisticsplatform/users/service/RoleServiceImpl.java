package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.utils.EntityChangeTracker;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.common.exception.DeleteConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.users.mapper.RoleMapper;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final RoleMapper roleMapper;

    @Cacheable(value = "roles", key = "'all-roles'")
    @Override
    public List<RoleResponseDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream().map(roleMapper::toResponse).toList();
    }

    @Cacheable(value = "roles", key = "#id")
    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with id: {}", id);
                    return new ResourceNotFoundException("Role not found with id: " + id);
                });

        return roleMapper.toResponse(role);
    }

    @CacheEvict(value = "roles", allEntries = true)
    @Override
    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        Optional<Role> existingRole = roleRepository.findByName(dto.getName());

        if (existingRole.isPresent()) {
            log.warn("Attempted to create duplicate role: {}", dto.getName());
            throw new DuplicateEntryException("name", dto.getName());
        }

        Role role = roleMapper.toEntity(dto);

        roleRepository.save(role);
        log.info("Role created: {}", dto.getName());
        this.logRole(role, AuditAction.CREATE);
        return roleMapper.toResponse(role);
    }

    @Caching(evict = {
            @CacheEvict(value = "roles", key = "'all-roles'"),
            @CacheEvict(value = "roles", key = "#id"),
            @CacheEvict(value = "users", allEntries = true)
    })
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
        return roleMapper.toResponse(updatedRole);
    }

    @Caching(evict = {
            @CacheEvict(value = "roles", key = "'all-roles'"),
            @CacheEvict(value = "roles", key = "#id")
    })
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

        EntityChangeTracker.trackFieldChange(changes, "name", Role::getName, oldRole, updatedRole);
        EntityChangeTracker.trackFieldChange(changes, "description", Role::getDescription, oldRole, updatedRole);

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
