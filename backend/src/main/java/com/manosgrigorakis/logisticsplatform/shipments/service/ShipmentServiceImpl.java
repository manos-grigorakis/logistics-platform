package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.mapper.ShipmentMapper;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.shipments.repository.ShipmentRepository;
import com.manosgrigorakis.logisticsplatform.shipments.repository.VehicleRepository;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    private final DocumentNumberGenerator documentNumberGenerator;

    private static final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    public ShipmentServiceImpl(
            ShipmentRepository shipmentRepository,
            QuoteRepository quoteRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            DocumentNumberGenerator documentNumberGenerator
    )
    {
        this.shipmentRepository = shipmentRepository;
        this.quoteRepository = quoteRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;

        this.documentNumberGenerator = documentNumberGenerator;
    }

    @Override
    public Page<ShipmentResponseDTO> getAllShipments(PageFilterRequest page, SortFilterRequest sort) {
        return null;
    }

    @Override
    public ShipmentResponseDTO getShipmentById(Long id) {
        Shipment shipment = findByIdOrThrow(id, shipmentRepository::findById, "Shipment");

        return ShipmentMapper.toResponse(shipment);
    }

    @Override
    public ShipmentResponseDTO createShipment(ShipmentRequestDTO dto) {
        // Required
        Quote quote = findByIdOrThrow(dto.getQuoteId(), quoteRepository::findById, "Quote");
        User createdByUser = findByIdOrThrow(dto.getCreatedByUserId(), userRepository::findById, "Created by user");

        // Nullable
        User driver = findByIdOrNull(dto.getDriverId(), userRepository::findById, "Driver");
        Vehicle truck = findByIdOrNull(dto.getTruckId(), vehicleRepository::findById, "Truck");
        Vehicle trailer = findByIdOrNull(dto.getTrailerId(), vehicleRepository::findById, "Trailer");

        Shipment shipment = ShipmentMapper.toEntity(dto, quote, driver, createdByUser, truck, trailer);

        if(quote.getQuoteStatus() != QuoteStatus.ACCEPTED) {
            log.warn("Attempted to create shipment for quote number {}", quote.getNumber());
            throw new ConflictException(
                    "Shipment can be created only for ACCEPTED quotes",
                    Map.of(
                            "quoteId", quote.getId(),
                            "quoteNumber", quote.getNumber(),
                            "currentStatus", quote.getQuoteStatus()
                            )
            );
        }

        if(shipmentRepository.existsByQuoteId(quote.getId())) {
            log.warn("Duplicate shipment with quote id: {}", quote.getId());
            throw new DuplicateEntryException("quoteId", quote.getId().toString());
        }

        if(shipment.getDriver() != null && !shipment.hasDriverRole()) {
            log.warn("Attempted to create shipment with non driver user");
            throw new ConflictException(
                    "Attempted to create shipment with non driver user",
                    Map.of("driverRole", shipment.getDriver().getRole().getName())
            );
        }

        // Find previous shipment number
        int currentYear = LocalDate.now().getYear();
        String lastNumber = shipmentRepository.findLastShipmentNumberByYear(currentYear)
                .orElse("TO-" + currentYear + "-0000");

        // Generate next shipment number and set it to shipment
        String newNumber = documentNumberGenerator.generateNextSequentialNumber("TO", lastNumber);
        shipment.setNumber(newNumber);

        // Save shipment
        Shipment savedShipment = shipmentRepository.save(shipment);
        log.info("Shipment created with number: {}", savedShipment.getNumber());

        return ShipmentMapper.toResponse(savedShipment);
    }

    @Override
    public ShipmentResponseDTO updateShipment(Long id, ShipmentRequestDTO dto) {
        return null;
    }

    /**
     * Finds an entity by its id, using the provider finder function
     * @param id The id of the entity
     * @param finder Function that retrieves the entity by id
     * @param modelName Field name for errors and logging
     * @return The founded entity
     * @param <T> The entity type
     * @throws ResourceNotFoundException if entity doesn't exist by id
     */
    private <T> T findByIdOrThrow(Long id, Function<Long, Optional<T>> finder, String modelName) {
        return finder.apply(id).orElseThrow(() -> {
            log.warn("{} not found with id: {}", modelName, id);
            return new ResourceNotFoundException(modelName + " not found with id: " + id);
        });
    }

    /**
     * Finds an entity by its id if provided
     * @param id The id of the entity, may be {@code null}
     * @param finder Function that retrieves the entity by id
     * @param modelName Field name for errors and logging
     * @return Founded entity or {@code null}
     * @param <T> The entity type
     * @throws ResourceNotFoundException if id is not {@code null} and entity doesn't exist
     */
    private <T> T findByIdOrNull(Long id, Function<Long, Optional<T>> finder, String modelName) {
        if (id == null) return null;
        return findByIdOrThrow(id, finder, modelName);
    }
}
