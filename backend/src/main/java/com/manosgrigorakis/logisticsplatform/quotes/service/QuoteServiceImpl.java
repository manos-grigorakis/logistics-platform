package com.manosgrigorakis.logisticsplatform.quotes.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.common.utils.EntityChangeTracker;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.mapper.QuoteItemMapper;
import com.manosgrigorakis.logisticsplatform.quotes.mapper.QuoteMapper;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.PdfService;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.specs.QuotesSpecs;
import com.manosgrigorakis.logisticsplatform.common.utils.FinancialCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.manosgrigorakis.logisticsplatform.common.utils.SpecsUtils.andIf;

@Service
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    private final PdfService pdfService;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    private final QuoteCalculator quoteCalculator;
    private final DocumentNumberGenerator documentNumberGenerator;

    private final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);

    @Value("${tax.vat}")
    private Integer vatPercent;

    public QuoteServiceImpl(
            QuoteRepository quoteRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PdfService pdfService,
            FileStorageService fileStorageService,
            AuditService auditService,
            QuoteCalculator quoteCalculator,
            DocumentNumberGenerator documentNumberGenerator) {
        this.quoteRepository = quoteRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.pdfService = pdfService;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
        this.quoteCalculator = quoteCalculator;
        this.documentNumberGenerator = documentNumberGenerator;
    }

    @Override
    public Page<QuoteListResponseDTO> getAllQuotes(
            QuoteFilterRequest quoteFilter,
            PageFilterRequest page,
            SortFilterRequest sort)
    {
        Specification<Quote> spec = Specification.allOf();
        spec = andIf(spec, quoteFilter.getNumber(), QuotesSpecs::likeNumber);
        spec = andIf(spec, quoteFilter.getCompanyName(), QuotesSpecs::likeCompanyName);
        spec = andIf(spec, quoteFilter.getQuoteStatus(), QuotesSpecs::equalQuoteStatus);

        Pageable pageable = PageRequest.of(page.getPage(), page.getSize(), sort.createSort());
        Page<Quote> quotePage = quoteRepository.findAll(spec, pageable);

        return quotePage.map(QuoteMapper::toListResponse);
    }

    @Override
    public QuoteResponseDTO getQuoteById(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Quote not found with id: {}", id);
                    return new ResourceNotFoundException("Quote not found with id: " + id);
                });

        String presignedUrl = fileStorageService.createPresignedUrl(quote.getNumber());

        QuoteResponseDTO response = QuoteMapper.toResponse(quote);
        response.setPdfUrl(presignedUrl);
        return response;
    }

    @Override
    public QuoteCreatedResponseDTO createQuote(QuoteRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.warn("Create failed. User not found with id: {}", dto.getUserId());
                    return new ResourceNotFoundException("User not found with id: " + dto.getUserId());
                });

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Create failed. Customer not found with id: {}", dto.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId());
                });

        Quote quote = QuoteMapper.toEntity(dto, user, customer);
        quote.setQuoteStatus(QuoteStatus.DRAFT);

        int currentYear = LocalDate.now().getYear();
        String lastNumber = quoteRepository.findLastQuoteNumberByYear(currentYear)
                .orElse("Q-" + currentYear + "-0000");

        String newNumber = documentNumberGenerator.generateNextSequentialNumber("Q", lastNumber);
        quote.setNumber(newNumber);

        BigDecimal netTotal = quoteCalculator.calculateNetTotal(quote);
        BigDecimal vatAmount = FinancialCalculator.calculateVatAmount(netTotal, vatPercent);
        BigDecimal grossTotal = FinancialCalculator.calculateGrossTotal(netTotal, vatAmount);

        quote.setTaxRatePercentage(vatPercent);
        quote.setNetPrice(netTotal.setScale(2, RoundingMode.HALF_UP));
        quote.setVatAmount(vatAmount.setScale(2, RoundingMode.HALF_UP));
        quote.setGrossPrice(grossTotal.setScale(2, RoundingMode.HALF_UP));
        Quote savedQuote = quoteRepository.save(quote);

        // Generate PDF and store / upload it
        byte[] quotePdf = pdfService.generateQuotePdf(quote);
        fileStorageService.store(quote.getNumber(), quotePdf, "application/pdf");

        String presignedUrl = fileStorageService.createPresignedUrl(quote.getNumber());
        QuoteCreatedResponseDTO response = QuoteMapper.toCreatedResponse(savedQuote);
        response.setPdfUrl(presignedUrl);

        log.info("Quote created with number {}", quote.getNumber());
        this.logCreatedQuote(quote);
        return response;
    }

    @Override
    public QuoteResponseDTO updateQuote(Long id, QuoteUpdateRequestDTO dto) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed. Quote not found with id: {}", id);
                    return new ResourceNotFoundException("Quote not found with id: " + id);
                });

        Quote oldQuote = new Quote(quote);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Update failed. Customer not found with id: {}", dto.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId());
                });

        if (!quote.isEditable()) {
            throw new ConflictException(
                    "Quote is not editable",
                    Map.of("quoteStatus", quote.getQuoteStatus())
            );
        }

        // Clear old items
        quote.getQuoteItems().clear();

        for(QuoteItemRequestDTO itemDto : dto.getQuoteItems()) {
            QuoteItem item = QuoteItemMapper.toEntity(itemDto);
            quote.addQuoteItem(item);
        }

        // Calculate
        BigDecimal netTotal = quoteCalculator.calculateNetTotal(quote);
        BigDecimal vatAmount = FinancialCalculator.calculateVatAmount(netTotal, vatPercent);
        BigDecimal grossTotal = FinancialCalculator.calculateGrossTotal(netTotal, vatAmount);

        // Update fields
        quote.setValidityDays(dto.getValidityDays());
        quote.setOrigin(dto.getOrigin());
        quote.setDestination(dto.getDestination());
        quote.setNotes(dto.getNotes());
        quote.setSpecialTerms(dto.getSpecialTerms());
        quote.setCustomer(customer);
        quote.setNetPrice(netTotal);
        quote.setVatAmount(vatAmount);
        quote.setGrossPrice(grossTotal);

        Quote savedQuote = quoteRepository.save(quote);

        // Re-generate PDF and store / upload it
        byte[] quotePdf = pdfService.generateQuotePdf(savedQuote);
        fileStorageService.store(savedQuote.getNumber(), quotePdf, "application/pdf");

        log.info("Quote updated with number: {}", savedQuote.getNumber());
        this.logUpdatedQuote(oldQuote, quote);

        // Create presigned url
        String presignedUrl = fileStorageService.createPresignedUrl(savedQuote.getNumber());

        // Set response
        QuoteResponseDTO response = QuoteMapper.toResponse(savedQuote);
        response.setPdfUrl(presignedUrl);
        return response;
    }

    @Override
    public void updateQuoteStatus(Long id, UpdateQuoteStatusRequestDTO dto) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Quote not found with id: {}", id);
                    return new ResourceNotFoundException("Quote not found with id: " + id);
                });

        Quote oldQuote = new Quote(quote);

        try {
            quote.changeStatusTo(dto.getQuoteStatus());
        } catch (IllegalStateException e) {
            throw new ConflictException(
                    e.getMessage(),
                    Map.of("currentStatus", quote.getQuoteStatus(), "desiredStatus", dto.getQuoteStatus())
            );
        }

        quote.setQuoteStatus(dto.getQuoteStatus());
        quoteRepository.save(quote);
        this.logUpdatedQuoteStatus(quote, oldQuote.getQuoteStatus(), quote.getQuoteStatus());
    }

    /**
     * Logs the quote in the audit system
     * @param quote The actual quote
     */
    private void logCreatedQuote(Quote quote) {
        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Quote")
                        .entityId(quote.getId())
                        .notes(
                                "Quote Number: " + quote.getNumber() +
                                        " | Customer: " + quote.getCustomer().getFullName()
                        )
                        .action(AuditAction.CREATE)
                        .build()
        );
    }

    /**
     * Logs updated quote in the audit system
     * @param oldQuote The quote entity before update
     * @param updatedQuote The updated quote entity
     */
    private void logUpdatedQuote(Quote oldQuote, Quote updatedQuote) {
        Map<String, Object> changes = new HashMap<>();

        EntityChangeTracker.trackFieldChange(changes, "customer",
                quote -> quote.getCustomer() != null ? quote.getCustomer().getFullName() : null,
                oldQuote, updatedQuote);

        EntityChangeTracker.trackFieldChange(changes, "origin", Quote::getOrigin ,oldQuote, updatedQuote);
        EntityChangeTracker.trackFieldChange(changes, "destination", Quote::getDestination ,oldQuote, updatedQuote);
        EntityChangeTracker.trackFieldChange(changes, "validityDays", Quote::getValidityDays ,oldQuote, updatedQuote);
        EntityChangeTracker.trackFieldChange(changes, "notes", Quote::getNotes ,oldQuote, updatedQuote);
        EntityChangeTracker.trackFieldChange(changes, "specialTerms", Quote::getSpecialTerms ,oldQuote, updatedQuote);

        if(changes.isEmpty()) return;

        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Quote")
                        .entityId(updatedQuote.getId())
                        .changes(changes)
                        .notes(
                                "Quote Number: " + updatedQuote.getNumber()
                                + " | Customer: " + updatedQuote.getCustomer().getFullName()
                        )
                        .action(AuditAction.UPDATE)
                        .build()
        );

    }

    /**
     * Logs the update quote status action in the audit system
     * @param quote The quote that status changed
     * @param oldStatus The quote status before update
     * @param updatedStatus The updated quote status
     */
    private void logUpdatedQuoteStatus(Quote quote, QuoteStatus oldStatus, QuoteStatus updatedStatus) {
        if(Objects.equals(oldStatus, updatedStatus)) return;

        Map<String, Object> changes = new HashMap<>();
        changes.put("status", Map.of(
                "old", oldStatus,
                "updated", updatedStatus
        ));

        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Quote")
                        .entityId(quote.getId())
                        .notes(
                                "Quote Number: " + quote.getNumber() +
                                        " | Customer: " + quote.getCustomer().getFullName()
                        )
                        .changes(changes)
                        .action(AuditAction.UPDATE)
                        .build()
        );
    }
}
