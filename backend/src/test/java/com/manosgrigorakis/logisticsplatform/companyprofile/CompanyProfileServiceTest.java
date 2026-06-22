package com.manosgrigorakis.logisticsplatform.companyprofile;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileCreateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileResponse;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileUpdateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import com.manosgrigorakis.logisticsplatform.companyprofile.repository.CompanyProfileRepository;
import com.manosgrigorakis.logisticsplatform.companyprofile.service.CompanyProfileServiceImpl;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyProfileServiceTest {
    @Mock
    private CompanyProfileRepository repository;

    @InjectMocks
    private CompanyProfileServiceImpl service;

    @Mock
    private FileStorageService storageService;

    private CompanyProfile mockCompany;
    private CompanyProfileCreateRequest mockCreateRequest;
    private CompanyProfileUpdateRequest mockUpdateRequest;
    private MockMultipartFile validFile;
    private MockMultipartFile wrongFileType;
    private MockMultipartFile tooLargeFile;

    @BeforeEach
    public void setUp() {
        mockCompany = CompanyProfile.builder().name("ACME Logistics").tin("123456789").build();

        // Requests
        mockCreateRequest = buildCreateRequest(null);

        mockUpdateRequest = buildUpdateRequest(null);
        // Files
        validFile = new MockMultipartFile("logo", "logo.png", "image/png", "logo".getBytes());
        wrongFileType = new MockMultipartFile("logo", "doc.pdf", "application/pdf", "document".getBytes());
        tooLargeFile = new MockMultipartFile("logo", "logo.png", "image/png", new byte[3_000_000]);
    }

    @Test
    void getCompanyProfile_ShouldReturnCompanyProfile_whenExists() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));

        // Act
        CompanyProfileResponse response = service.getCompanyProfile();

        // Assert
        assertNotNull(response);
        assertEquals(mockCompany.getName(), response.name());
        assertEquals(mockCompany.getTin(), response.tin());
        verify(repository, times(1)).findFirstByOrderByIdAsc();
    }

    @Test
    void getCompanyProfile_ShouldGeneratePresignedUrl_whenLogoExists() {
        // Arrange
        CompanyProfile companyWithLogo = CompanyProfile.builder().name("ACME Logistics").tin("123456789").logoUrl(
                "random-key").build();
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(companyWithLogo));

        // Act
        service.getCompanyProfile();

        // Assert
        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(storageService, times(1)).createPresignedUrl(anyString());
    }

    @Test
    void getCompanyProfile_shouldThrowResourceNotFoundException_whenCompanyProfileNotExists() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getCompanyProfile());
        verify(repository, times(1)).findFirstByOrderByIdAsc();
    }

    @Test
    void createCompanyProfile_shouldCreateCompanyProfile_whenNoOtherCompanyExists() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Act
        CompanyProfileResponse response = service.createCompanyProfile(mockCreateRequest);

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository, times(1)).save(captor.capture());
        verify(repository, times(1)).findFirstByOrderByIdAsc();

        CompanyProfile saved = captor.getValue();
        assertNotNull(response);
        assertEquals("ACME Logistics", saved.getName());
        assertEquals("123456789", saved.getTin());
        assertNull(saved.getLogoUrl());
    }

    @Test
    void createCompanyProfile_shouldCreateCompanyProfileAndStoreUploadedFile_whenFileValid() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        CompanyProfileCreateRequest mockCreateRequestValidFile = buildCreateRequest(validFile);

        // Act
        CompanyProfileResponse response = service.createCompanyProfile(mockCreateRequestValidFile);

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, times(1)).save(captor.capture());
        verify(storageService, times(1)).store(anyString(), any(byte[].class), anyString());
        verify(storageService, times(1)).createPresignedUrl(anyString());

        CompanyProfile saved = captor.getValue();
        assertNotNull(response);
        assertEquals("ACME Logistics", saved.getName());
        assertEquals("123456789", saved.getTin());
        assertNotNull(saved.getLogoUrl());
    }

    @Test
    void createCompanyProfile_shouldThrowBadRequestException_whenFileContentTypeIsInvalid() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        CompanyProfileCreateRequest mockCreateRequestWrongFileType = buildCreateRequest(wrongFileType);

        // Act
        BadRequestException exc = assertThrows(BadRequestException.class, () ->
                service.createCompanyProfile(mockCreateRequestWrongFileType));

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        assertEquals("INVALID_FILE_TYPE", exc.getErrorCode());

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, never()).save(captor.capture());
        verify(storageService, never()).store(anyString(), any(byte[].class), anyString());
    }

    @Test
    void createCompanyProfile_shouldThrowBadRequestException_whenFileSizeIsTooLarge() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        CompanyProfileCreateRequest mockCreateRequestFileTooLarge = buildCreateRequest(tooLargeFile);

        // Act
        BadRequestException exc = assertThrows(BadRequestException.class,
                                               () -> service.createCompanyProfile(mockCreateRequestFileTooLarge));

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        assertEquals("FILE_TOO_LARGE", exc.getErrorCode());

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, never()).save(captor.capture());
        verify(storageService, never()).store(anyString(), any(byte[].class), anyString());
    }

    @Test
    void createCompanyProfile_shouldThrowConflictException_whenCompanyProfileAlreadyExists() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(new CompanyProfile()));

        // Act && Assert
        ConflictException exc = assertThrows(ConflictException.class,
                                             () -> service.createCompanyProfile(mockCreateRequest));
        assertEquals("COMPANY_PROFILE_ALREADY_EXISTS", exc.getErrorCode());
    }

    @Test
    void createCompanyProfile_shouldDeleteUploadedFile_whenDbSaveFails() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        when(repository.save(any(CompanyProfile.class))).thenThrow(new RuntimeException("DB error"));
        CompanyProfileCreateRequest request = buildCreateRequest(validFile);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createCompanyProfile(request));

        verify(storageService, times(1)).store(anyString(), any(byte[].class), anyString());
        verify(storageService, times(1)).deleteObject(anyString());
    }

    @Test
    void updateCompanyProfile_shouldUpdateCompanyProfile() {
        // Assert
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));

        // Act
        CompanyProfileResponse response = service.updateCompanyProfile(mockUpdateRequest);

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        verify(repository, times(1)).save(captor.capture());
        verify(repository, times(1)).findFirstByOrderByIdAsc();

        CompanyProfile saved = captor.getValue();
        assertNotNull(response);
        assertEquals("ACME Logistics", saved.getName());
        assertEquals("123456789", saved.getTin());
        assertNull(saved.getLogoUrl());
    }

    @Test
    void updateCompanyProfile_shouldUpdateCompanyProfileAndStoreUploadedFile_whenFileValid() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));
        CompanyProfileUpdateRequest mockUpdateRequestValidFile = buildUpdateRequest(validFile);

        // Act
        CompanyProfileResponse response = service.updateCompanyProfile(mockUpdateRequestValidFile);

        // Assert
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, times(1)).save(captor.capture());
        verify(storageService, times(1)).store(anyString(), any(byte[].class), anyString());
        verify(storageService, times(1)).createPresignedUrl(anyString());

        CompanyProfile saved = captor.getValue();
        assertNotNull(response);
        assertEquals("ACME Logistics", saved.getName());
        assertEquals("123456789", saved.getTin());
        assertNotNull(saved.getLogoUrl());
    }

    @Test
    void updateCompanyProfile_shouldThrowBadRequestException_whenFileContentTypeIsInvalid() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));
        CompanyProfileUpdateRequest mockUpdateRequestValidFile = buildUpdateRequest(wrongFileType);

        // Act
        BadRequestException exc = assertThrows(BadRequestException.class,
                                               () -> service.updateCompanyProfile(mockUpdateRequestValidFile));

        // Assert
        assertEquals("INVALID_FILE_TYPE", exc.getErrorCode());

        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, never()).save(captor.capture());
        verify(storageService, never()).store(anyString(), any(byte[].class), anyString());
    }

    @Test
    void updateCompanyProfile_shouldThrowBadRequestException_whenFileSizeIsTooLarge() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));
        CompanyProfileUpdateRequest mockUpdateRequestValidFile = buildUpdateRequest(tooLargeFile);

        // Act
        BadRequestException exc = assertThrows(BadRequestException.class,
                                               () -> service.updateCompanyProfile(mockUpdateRequestValidFile));

        // Assert
        assertEquals("FILE_TOO_LARGE", exc.getErrorCode());

        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);

        verify(repository, times(1)).findFirstByOrderByIdAsc();
        verify(repository, never()).save(captor.capture());
        verify(storageService, never()).store(anyString(), any(byte[].class), anyString());
    }

    @Test
    void updateCompanyProfile_shouldThrowResourceNotFoundException_whenCompanyProfileNotExists() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.updateCompanyProfile(mockUpdateRequest));
    }

    @Test
    void updateCompanyProfile_shouldDeleteUploadedFile_whenDbSaveFails() {
        // Arrange
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockCompany));
        when(repository.save(any(CompanyProfile.class))).thenThrow(new RuntimeException("DB error"));
        CompanyProfileUpdateRequest request = buildUpdateRequest(validFile);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateCompanyProfile(request));

        verify(storageService, times(1)).store(anyString(), any(byte[].class), anyString());
        verify(storageService, times(1)).deleteObject(anyString());
    }

    /**
     * Builds a {@link CompanyProfileCreateRequest} object to be used in tests
     *
     * @param file The file to be provided for testing different cases
     * @return The created object
     */
    private CompanyProfileCreateRequest buildCreateRequest(MultipartFile file) {
        return new CompanyProfileCreateRequest(
                "ACME Logistics",
                "123456789",
                24,
                "CEO",
                "John Doe",
                "Street",
                "100",
                "123456",
                "Athens",
                "Greece",
                "#FFFFFF",
                "#FFFFFF",
                file,
                "https://example.com",
                "Random slogan",
                List.of("12345678", "87654321"),
                "doe@example.com"
                );
    }

    /**
     * Builds a {@link CompanyProfileUpdateRequest} object to be used in tests
     *
     * @param file The file to be provided for testing different cases
     * @return The created object
     */
    private CompanyProfileUpdateRequest buildUpdateRequest(MultipartFile file) {
        return new CompanyProfileUpdateRequest(
                "ACME Logistics",
                24,
                "CEO",
                "John Doe",
                "Street",
                "100",
                "123456",
                "Athens",
                "Greece",
                "#FFFFFF",
                "#FFFFFF",
                file,
                "https://example.com",
                "Random slogan",
                List.of("12345678", "87654321"),
                "doe@example.com");
    }
}
