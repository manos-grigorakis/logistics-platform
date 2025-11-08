package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.user.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.user.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.mapper.UserMapper;
import com.manosgrigorakis.logisticsplatform.model.Role;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserTokensServiceImpl userTokensService;

    @Value("${app.setup_password.expires:48h}")
    private Duration setupPasswordTokenExpirationTime;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           UserTokensServiceImpl userTokensService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userTokensService = userTokensService;
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + dto.getRoleId()));

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .status(UserStatus.INVITED)
                .build();

        user.setRole(role);

        userRepository.save(user);

        // Generate token
        UserTokens userTokens = userTokensService.generateUserTokens(
                TokenType.CREATE_PASSWORD, setupPasswordTokenExpirationTime, user);

        // TODO: send email to user to setup password

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO updateUserById(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + dto.getRoleId()));

        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new RuntimeException("Email already exists");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(role);

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
