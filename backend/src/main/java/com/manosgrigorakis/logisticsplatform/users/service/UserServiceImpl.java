package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.utils.EntityChangeTracker;
import com.manosgrigorakis.logisticsplatform.infrastructure.mail.MailService;
import com.manosgrigorakis.logisticsplatform.auth.service.UserTokensServiceImpl;
import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.auth.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.users.mapper.UserMapper;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserTokensServiceImpl userTokensService;
    private final MailService mailService;
    private final AuditService auditService;
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${app.setup_password.expires:48h}")
    private Duration setupPasswordTokenExpirationTime;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           UserTokensServiceImpl userTokensService, MailService mailService, AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userTokensService = userTokensService;
        this.mailService = mailService;
        this.auditService = auditService;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users =  userRepository.findAll();

        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent()) {
            log.warn("Attempted to create duplicate user with email: {}", dto.getEmail());
            throw new DuplicateEntryException("email", dto.getEmail());
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> {
                    log.error("Create user failed. Role not found with id: {}", dto.getRoleId());
                    return new ResourceNotFoundException("Role not found with id: " + dto.getRoleId());
                });

        User user = UserMapper.toEntity(dto, role);
        user.setStatus(UserStatus.INVITED);

        userRepository.save(user);
        log.info("User created: {}", user.getEmail());
        this.logUser(user, AuditAction.CREATE);

        // Generate token
        UserTokens userTokens = userTokensService.generateUserTokens(
                TokenType.CREATE_PASSWORD, setupPasswordTokenExpirationTime, user);

        log.info("Password setup token created for user: {}", user.getEmail());

        // Send mail
        mailService.sendSetupPasswordMail(user, userTokens.getToken());
        log.info("Password setup email sent to {}", user.getEmail());

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO updateUserById(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                        log.error("Update failed. User not found with id: {}", id);
                        return new ResourceNotFoundException("User not found with id: " + id);
                });

        User oldUser = new User(user);

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> {
                    log.error("Updated failed. Role not found with id: {}", dto.getRoleId());
                    return new ResourceNotFoundException("Role not found with id: " + dto.getRoleId());
                });

        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            log.warn("Update failed. Attempted to create duplicate user: {}", dto.getEmail());
            throw new DuplicateEntryException("email", dto.getEmail());
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(role);
        userRepository.save(user);
        log.info("User updated: {}", dto.getEmail());
        this.logUpdatedUser(oldUser, user);
        return UserMapper.toResponse(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Delete failed. User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
        this.logUser(user, AuditAction.DELETE);
    }

    /**
     * Logs user in the audit system
     * @param user The actual user
     * @param action The action take {@link AuditAction}
     */
    private void logUser(User user, AuditAction action) {
        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("User")
                        .entityId(user.getId())
                        .action(action)
                        .notes("Email: " + user.getEmail())
                        .build()
        );
    }

    /**
     * Logs updated user in the audit system with the changed fields only
     * @param oldUser The user before update
     * @param updatedUser The updated user
     */
    private void logUpdatedUser(User oldUser, User updatedUser) {
        Map<String, Object> changes = new HashMap<>();

        EntityChangeTracker.trackFieldChange(changes, "firstName", User::getFirstName, oldUser, updatedUser);
        EntityChangeTracker.trackFieldChange(changes, "lastName", User::getLastName, oldUser, updatedUser);
        EntityChangeTracker.trackFieldChange(changes, "email", User::getEmail, oldUser, updatedUser);
        EntityChangeTracker.trackFieldChange(changes, "phone", User::getPhone, oldUser, updatedUser);
        EntityChangeTracker.trackFieldChange(changes, "role",
                user -> user.getRole() != null ? user.getRole().getName() : null
                , oldUser, updatedUser);

        if(changes.isEmpty()) return;

        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("User")
                        .entityId(updatedUser.getId())
                        .changes(changes)
                        .action(AuditAction.UPDATE)
                        .build()
        );
    }
}
