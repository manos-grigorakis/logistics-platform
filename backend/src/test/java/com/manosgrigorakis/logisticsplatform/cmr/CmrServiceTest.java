package com.manosgrigorakis.logisticsplatform.cmr;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.cmr.repository.CmrDocumentRepository;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentServiceImpl;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CmrServiceTest {
    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private CmrDocumentRepository cmrDocumentRepository;

    @InjectMocks
    private CmrDocumentServiceImpl cmrDocumentService;

    @Test
    public void getCmrDocumentById_shouldReturnCmrDocument_whenExists() {
        // Arrange
        CmrDocument cmrDocument = new CmrDocument();
        cmrDocument.setNumber("CMR-2026-0001");
        when(cmrDocumentRepository.findById(1L)).thenReturn(Optional.of(cmrDocument));

        // Act
        CmrDocumentResponseDTO responseDTO = cmrDocumentService.getCmrDocumentById(1L);

        // Assert
        assertEquals("CMR-2026-0001", responseDTO.getNumber());
    }

    @Test
    public void getCmrDocumentById_shouldThrowNotFound_whenIdDoesNotExist() {
        // Arrange
        when(cmrDocumentRepository.findById(1000L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cmrDocumentService.getCmrDocumentById(1000L));
    }
}
