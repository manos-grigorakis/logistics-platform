package com.manosgrigorakis.logisticsplatform.users;

import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.RoleResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.service.RoleServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class RoleServiceTest {
    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Test
    void createRole_shouldCreateRole() {
        // Arrange
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("manager");

        // Act
        RoleResponseDTO responseDTO = roleService.createRole(dto);
        Role role = roleRepository.findById(responseDTO.getId())
                .orElseThrow();

        // Assert
        assertEquals("MANAGER", role.getName());
    }

    @Test
    void createRole_shouldThrow_whenRoleExists() {
        // Arrange
        roleRepository.save(createRole("admin", true));

        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("admin");

        // Act & Assert
        assertThrows(DuplicateEntryException.class,
                () -> roleService.createRole(dto));
    }

    @Test
    void updateRole_shouldUpdateRole() {
        // Arrange
        Role role = roleRepository.save(createRole("manage", true));

        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("manager");

        // Act
        RoleResponseDTO updatedRole = roleService.updateRole(role.getId(), dto);

        // Assert
        assertEquals(role.getId(), updatedRole.getId());
        assertEquals("MANAGER", updatedRole.getName());
    }

    @Test
    void updateRole_shouldThrow_whenNotFound() {
        // Arrange
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("manager");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.updateRole(9999L, dto));
    }

    @Test
    void updateRole_shouldThrow_whenIsNotEditable() {
        // Arrange
        Role role = roleRepository.save(createRole("admin", false));

        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("super_admin");

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> roleService.updateRole(role.getId(), dto));
    }

    @Test
    void updateRole_shouldThrow_whenRoleExists() {
        // Arrange
        Role existingRole = roleRepository.save(createRole("manager", true));
        Role role = roleRepository.save(createRole("driver", true));

        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setName("manager");

        // Act & Assert
        assertThrows(DuplicateEntryException.class,
                () -> roleService.updateRole(role.getId(), dto));
    }

    private Role createRole(String name, boolean isEditable) {
        Role role = new Role();
        role.setName(name);
        role.setEditable(isEditable);
        return role;
    }
}
