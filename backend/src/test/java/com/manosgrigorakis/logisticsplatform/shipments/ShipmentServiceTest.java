package com.manosgrigorakis.logisticsplatform.shipments;

import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
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
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.UpdateShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.UpdateShipmentStatusRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.CmrDocumentSummary;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
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

import java.util.ArrayList;
import java.util.List;
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
        User driver = new User();
        Shipment shipment = buildShipment();
        shipment.setNumber("SH-2026-0001");
        shipment.setDriver(driver);

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
        Quote quote = buildQuote(QuoteStatus.DRAFT);

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
        Quote quote = buildQuote(QuoteStatus.ACCEPTED);

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
        Quote quote = buildQuote(QuoteStatus.ACCEPTED);

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

    @Test
    public void updateShipment_shouldSaveShipment() {
        // Arrange
        Shipment shipment = buildShipment();

        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        Vehicle truck = new Vehicle();
        truck.setId(1L);
        truck.setPlate("ABC-2020");
        truck.setType(VehicleType.TRUCK);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(truck));

        UpdateShipmentRequestDTO request = new UpdateShipmentRequestDTO();
        request.setTruckId(1L);
        request.setCargoItems(new ArrayList<>());

        // Act
        shipmentService.updateShipmentById(1L, request);

        // Assert
        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        Shipment updatedShipment = captor.getValue();

        assertEquals(truck.getId(), updatedShipment.getTruck().getId());
        assertEquals(truck.getPlate(), updatedShipment.getTruck().getPlate());
    }

    @Test
    public void updateShipmentById_shouldThrowNotFoundException() {
        // Arrange
        when(shipmentRepository.findById(1000L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                shipmentService.updateShipmentById(1000L, new UpdateShipmentRequestDTO()));
    }

    @Test
    public void updateShipmentById_shouldThrowConflictException_whenShipmentStatusIsNotEditable() {
        // Arrange
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // Act & Assert
        assertThrows(ConflictException.class, () ->
                shipmentService.updateShipmentById(1L, new UpdateShipmentRequestDTO()));
    }

    @Test
    public void updateShipmentStatus_shouldUpdateShipmentStatus() {
        // Arrange
        User driver = new User();
        Vehicle truck = new Vehicle();
        Vehicle trailer = new Vehicle();
        truck.setType(VehicleType.TRUCK);
        trailer.setType(VehicleType.TRAILER);

        List<ShipmentCargo> shipmentCargos = new ArrayList<>();
        shipmentCargos.add(new ShipmentCargo());

        Shipment shipment = buildShipment();
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setShipmentCargos(shipmentCargos);
        shipment.setDriver(driver);
        shipment.setTruck(truck);
        shipment.setTrailer(trailer);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // Act
        shipmentService.updateShipmentStatus(1L, new UpdateShipmentStatusRequestDTO(ShipmentStatus.DISPATCHED));

        // Assert
        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        Shipment updatedShipment = captor.getValue();
        assertEquals(ShipmentStatus.DISPATCHED, updatedShipment.getStatus());
    }

    @Test
    public void updateShipmentStatus_shouldThrowNotFoundException_whenShipmentDoesNotExist() {
        // Arrange
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                shipmentService.updateShipmentById(1L, new UpdateShipmentRequestDTO()));
    }

    @Test
    public void updateShipmentStatus_shouldThrowConflictException_whenShipmentIsNotEditable() {
        // Arrange
        Shipment shipment = buildShipment();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.DELIVERED);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // Act & Assert
        assertThrows(ConflictException.class, () ->
                shipmentService.updateShipmentById(1L, new UpdateShipmentRequestDTO()));
    }

    @Test
    public void getCmrDocumentByShipmentId_shouldReturnCmrDocument() {
        // Arrange
        Shipment shipment = new Shipment();
        CmrDocument cmrDocument = new CmrDocument();
        cmrDocument.setNumber("CMR-2026-0001");

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(cmrDocumentRepository.findCmrDocumentByShipmentId(1L)).thenReturn(Optional.of(cmrDocument));

        // Act
        CmrDocumentSummary response = shipmentService.getCmrDocumentByShipmentId(1L);

        // Assert
        assertEquals(cmrDocument.getNumber(), response.number());
    }

    @Test
    public void getCmrDocumentByShipmentId_shouldThrowNotFoundException_whenShipmentDoesNotExist() {
        // Arrange
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> shipmentService.getCmrDocumentByShipmentId(1L));
    }

    /**
     * Builds a {@link Shipment} to be used for testing purposes
     * <p>The shipment includes:</p>
     * <ul>
     *     <li>A {@link Shipment#quote}</li>
     *     <li>A {@link Shipment#createdByUser}</li>
     *     <li>A {@link Shipment#shipmentCargos}</li>
     * </ul>
     * @return The preconfigured {@link Shipment} for testing
     */
    private Shipment buildShipment() {
        Shipment shipment = new Shipment();
        shipment.setQuote(new Quote());
        shipment.setCreatedByUser(new User());
        shipment.setShipmentCargos(new ArrayList<>());
        return shipment;
    }

    /**
     * Builds a {@link Quote} to be used for testing purposes
     * <p>The quote includes</p>
     * <ul>
     *     <li>{@link Quote#id} -> {@code 1L}</li>
     *     <li>{@link Quote#number} -> {@code Q-2026-0001}</li>
     *     <li>{@link Quote#quoteStatus} -> The provided status</li>
     * </ul>
     * @param status The {@link QuoteStatus} to be set on the created quote
     * @return The preconfigured {@link Quote} for testing
     */
    private Quote buildQuote(QuoteStatus status) {
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setNumber("Q-2026-0001");
        quote.setQuoteStatus(status);
        return quote;
    }
}