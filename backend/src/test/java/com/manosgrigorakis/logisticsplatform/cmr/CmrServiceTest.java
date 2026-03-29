package com.manosgrigorakis.logisticsplatform.cmr;

import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.cmr.repository.CmrDocumentRepository;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentServiceImpl;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.generators.CmrDocumentPdfGenerator;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CmrServiceTest {
    @Mock
    private DocumentNumberGenerator documentNumberGenerator;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private CmrDocumentPdfGenerator cmrDocumentPdfGenerator;

    @Mock
    private AuditService auditService;

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

    @Test
    public void createCmrDocument_shouldGenerateNextNumber_whenCmrExists() {
        // Arrange
        Quote quote = new Quote();
        Shipment shipment = new Shipment();

        when(cmrDocumentRepository.findLastCmrDocumentNumberByYear(LocalDate.now().getYear()))
                .thenReturn(Optional.of("CMR-2026-0001"));
        when(documentNumberGenerator.generateNextSequentialNumber("CMR", "CMR-2026-0001"))
                .thenReturn("CMR-2026-0002");
        when(cmrDocumentPdfGenerator.generatePdf(any())).thenReturn(new byte[0]);

        // Act
        cmrDocumentService.createCmrDocument(quote, shipment);

        // Assert
        ArgumentCaptor<CmrDocument> argumentCaptor = ArgumentCaptor.forClass(CmrDocument.class);
        verify(cmrDocumentRepository).findLastCmrDocumentNumberByYear(LocalDate.now().getYear());
        verify(cmrDocumentRepository).save(argumentCaptor.capture());
        CmrDocument saved = argumentCaptor.getValue();

        assertEquals("CMR-2026-0002", saved.getNumber());
    }

    @Test
    public void createCmrDocument_shouldGenerateFirstNumber_whenCmrDoesNotExist() {
        // Arrange
        Quote quote = new Quote();
        Shipment shipment = new Shipment();

        when(cmrDocumentRepository.findLastCmrDocumentNumberByYear(LocalDate.now().getYear()))
                .thenReturn(Optional.empty());
        when(documentNumberGenerator.generateNextSequentialNumber("CMR", "CMR-2026-0000"))
                .thenReturn("CMR-2026-0001");
        when(cmrDocumentPdfGenerator.generatePdf(any())).thenReturn(new byte[0]);

        // Act
        cmrDocumentService.createCmrDocument(quote, shipment);

        // Assert
        ArgumentCaptor<CmrDocument> argumentCaptor = ArgumentCaptor.forClass(CmrDocument.class);
        verify(cmrDocumentRepository).findLastCmrDocumentNumberByYear(LocalDate.now().getYear());
        verify(cmrDocumentRepository).save(argumentCaptor.capture());
        CmrDocument saved = argumentCaptor.getValue();

        assertEquals("CMR-2026-0001", saved.getNumber());
    }
}
