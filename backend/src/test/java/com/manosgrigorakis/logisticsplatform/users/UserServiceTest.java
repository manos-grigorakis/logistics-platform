package com.manosgrigorakis.logisticsplatform.users;

import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.infrastructure.mail.MailService;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.users.service.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class UserServiceTest {
    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @MockitoBean
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void getUser_shouldReturnUser() {
        // Assert
        Role role = createAndSaveRole();
        User existingUser = createAndSaveUser("example@example.com", role);

        // Act
        UserResponseDTO foundedUser = userService.getUserById(existingUser.getId());

        // Assert
        assertEquals(existingUser.getId(), foundedUser.getId());
        assertEquals(existingUser.getEmail(), foundedUser.getEmail());
        assertEquals(existingUser.getRole().getId(), foundedUser.getRoleId());
    }

    @Test
    void getUser_shouldThrow_whenUserNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(9999L));
    }

    @Test
    void createUser_shouldCreateInvitedUser() {
        // Assert
        Role role = createAndSaveRole();
        UserRequestDTO dto = createUserDTO(role.getId());

        // Act
        UserResponseDTO savedUser = userService.createUser(dto);

        // Assert
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals(UserStatus.INVITED, savedUser.getStatus());
        assertEquals(role.getId(), savedUser.getRoleId());
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        // Assert
        Role role = createAndSaveRole();
        createAndSaveUser("john.doe@example.com", role);
        UserRequestDTO dto = createUserDTO(role.getId());

        // Act & Assert
        assertThrows(DuplicateEntryException.class,
                () -> userService.createUser(dto));
    }

    @Test
    void createUser_shouldThrow_whenRoleNotFound() {
        UserRequestDTO dto = createUserDTO(9999L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(dto));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // Assert
        Role role = createAndSaveRole();
        User existingUser = createAndSaveUser("example@example.com", role);

        UserRequestDTO dto = createUserDTO(role.getId());

        // Act
        UserResponseDTO response = userService.updateUserById(existingUser.getId(), dto);

        // Assert
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(role.getId(), response.getRoleId());
    }

    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        // Assert
        Role role = createAndSaveRole();
        UserRequestDTO dto = createUserDTO(role.getId());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserById(9999L, dto));
    }

    @Test
    void updateUser_shouldThrow_whenRoleNotFound() {
        // Assert
        Role role = createAndSaveRole();
        User existingUser = createAndSaveUser("example@example.com", role);
        UserRequestDTO dto = createUserDTO(9999L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserById(existingUser.getId(), dto));
    }

    @Test
    void updateUser_shouldThrow_whenEmailExists() {
        // Assert
        Role role = createAndSaveRole();
        createAndSaveUser("john.doe@example.com", role); // Existing user
        User userToUpdate = createAndSaveUser("example@example.com", role);
        UserRequestDTO dto = createUserDTO(role.getId());

        // Act & Assert
        assertThrows(DuplicateEntryException.class,
                () -> userService.updateUserById(userToUpdate.getId(), dto));
    }

    private Role createAndSaveRole() {
        return roleRepository.save(Role.builder().name("DRIVER").build());
    }

    private User createAndSaveUser(String email, Role role) {
        User user = User.builder()
                .firstName("Maria")
                .lastName("Papadopoulou")
                .email(email)
                .status(UserStatus.ACTIVE)
                .build();

        user.setRole(role);

        return userRepository.save(user);
    }

    private UserRequestDTO createUserDTO(Long roleId) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setRoleId(roleId);
        return dto;
    }
}
