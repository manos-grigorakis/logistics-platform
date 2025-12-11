package com.manosgrigorakis.logisticsplatform.users;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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
}
