package com.manosgrigorakis.logisticsplatform.shipments;

import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.cmr.repository.CmrDocumentRepository;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentService;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.repository.ShipmentRepository;
import com.manosgrigorakis.logisticsplatform.shipments.repository.VehicleRepository;
import com.manosgrigorakis.logisticsplatform.shipments.service.ShipmentServiceImpl;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {
    @Mock
    private DocumentNumberGenerator documentNumberGenerator;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CmrDocumentRepository cmrDocumentRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private CmrDocumentService cmrDocumentService;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    @Test
    public void getShipmentById_shouldReturnResponse() {
        // Arrange
        Shipment shipment = new Shipment();
        Quote quote = new Quote();
        User createdBy = new User();
        User driver = new User();

        shipment.setNumber("SH-2026-0001");
        shipment.setQuote(quote);
        shipment.setDriver(driver);
        shipment.setCreatedByUser(createdBy);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // Act
        ShipmentResponseDTO response = shipmentService.getShipmentById(1L);

        // Assert
        assertEquals("SH-2026-0001", response.number());
    }

    @Test
    public void getShipmentById_shouldThrowNotFoundException() {
        // Arrange
        when(shipmentRepository.findById(1000L)).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(ResourceNotFoundException.class, () -> shipmentService.getShipmentById(1000L));
    }

    @Test
    public void createShipment_shouldThrowNotFoundException_whenQuoteDoesNotExist() {
        // Arrange
        when(quoteRepository.findById(1000L)).thenReturn(Optional.empty());

        ShipmentRequestDTO request = new ShipmentRequestDTO();
        request.setQuoteId(1000L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                shipmentService.createShipment(request));
    }

    @Test
    public void createShipment_shouldThrowNotFoundException_whenCreatedByUserDoesNotExist() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(new Quote()));
        when(userRepository.findById(1000L)).thenReturn(Optional.empty());

        ShipmentRequestDTO request = new ShipmentRequestDTO();
        request.setQuoteId(1L);
        request.setCreatedByUserId(1000L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> shipmentService.createShipment(request));
    }

    @Test
    public void createShipment_shouldThrowConflictException_whenQuoteStatusIsNotAccepted() {
        // Arrange
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setNumber("Q-2026-0001");
        quote.setQuoteStatus(QuoteStatus.DRAFT);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        ShipmentRequestDTO request = new ShipmentRequestDTO();
        request.setQuoteId(1L);
        request.setCreatedByUserId(1L);

        // Act & Assert
        assertThrows(ConflictException.class, () -> shipmentService.createShipment(request));
    }

    @Test
    public void createShipment_shouldThrowDuplicateException_whenQuoteIsAlreadyExist() {
        // Arrange
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setNumber("Q-2026-0001");
        quote.setQuoteStatus(QuoteStatus.ACCEPTED);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(shipmentRepository.existsByQuoteId(1L)).thenReturn(true);

        ShipmentRequestDTO request = new ShipmentRequestDTO();
        request.setQuoteId(1L);
        request.setCreatedByUserId(1L);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> shipmentService.createShipment(request));
    }

    @Test
    public void createShipment_shouldSaveShipmentAndQuoteStatusToConverted() {
        // Arrange
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setNumber("Q-2026-0001");
        quote.setQuoteStatus(QuoteStatus.ACCEPTED);

        User createdBy = new User();

        Shipment savedShipment = new Shipment();
        savedShipment.setQuote(quote);
        savedShipment.setCreatedByUser(createdBy);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(shipmentRepository.existsByQuoteId(1L)).thenReturn(false);
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(savedShipment);

        ShipmentRequestDTO request = new ShipmentRequestDTO();
        request.setQuoteId(1L);
        request.setCreatedByUserId(1L);

        // Act
        shipmentService.createShipment(request);

        // Assert
        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        Shipment shipment = captor.getValue();
        assertEquals(QuoteStatus.CONVERTED, shipment.getQuote().getQuoteStatus());
    }
}