package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.mapper.QuoteItemMapper;
import com.manosgrigorakis.logisticsplatform.mapper.QuoteMapper;
import com.manosgrigorakis.logisticsplatform.model.Customer;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.service.FileStorageService;
import com.manosgrigorakis.logisticsplatform.service.QuoteService;
import com.manosgrigorakis.logisticsplatform.specs.QuotesSpecs;
import com.manosgrigorakis.logisticsplatform.utils.FinancialCalculator;
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
import java.util.List;
import java.util.Map;

import static com.manosgrigorakis.logisticsplatform.utils.SpecsUtils.andIf;

@Service
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    private final PdfService pdfService;
    private final FileStorageService fileStorageService;

    private final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);

    @Value("${tax.vat}")
    private Integer vatPercent;

    public QuoteServiceImpl(
            QuoteRepository quoteRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PdfService pdfService,
            FileStorageService fileStorageService)
    {
        this.quoteRepository = quoteRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.pdfService = pdfService;
        this.fileStorageService = fileStorageService;
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

        String newNumber = generateNextQuoteNumber(lastNumber);
        quote.setNumber(newNumber);

        BigDecimal netTotal = calculateNetTotal(quote);
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
        return response;
    }

    @Override
    public QuoteResponseDTO updateQuote(Long id, QuoteUpdateRequestDTO dto) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed. Quote not found with id: {}", id);
                    return new ResourceNotFoundException("Quote not found with id: " + id);
                });

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

        // Update fields
        quote.setValidityDays(dto.getValidityDays());
        quote.setOrigin(dto.getOrigin());
        quote.setDestination(dto.getDestination());
        quote.setNotes(dto.getNotes());
        quote.setSpecialTerms(dto.getSpecialTerms());
        quote.setUser(quote.getUser());
        quote.setCustomer(quote.getCustomer());

        // Clear old items
        quote.getQuoteItems().clear();

        for(QuoteItemRequestDTO itemDto : dto.getQuoteItems()) {
            QuoteItem item = QuoteItemMapper.toEntity(itemDto);
            quote.addQuoteItem(item);
        }

        Quote savedQuote = quoteRepository.save(quote);
        log.info("Quote updated with number: {}", quote.getNumber());
        // Create presigned url
        String presignedUrl = fileStorageService.createPresignedUrl(quote.getNumber());

        QuoteResponseDTO response = QuoteMapper.toResponse(savedQuote);
        response.setPdfUrl(presignedUrl);
        return response;
    }

    /**
     * Calculate the net total of a Quote
     * The method sums the total price of all related quote item (pre-tax)
     * @param quote The Quote entity containing the related Quote Items
     * @return net total
     */
    private BigDecimal calculateNetTotal(Quote quote) {
        BigDecimal netTotal = BigDecimal.ZERO;

        // Get quote Items
        List<QuoteItem> quoteItems = quote.getQuoteItems();

        // Add each item price
        for(QuoteItem quoteItem : quoteItems) {
            BigDecimal itemPrice = quoteItem.getPrice();

            if(itemPrice != null) {
                netTotal = netTotal.add(itemPrice);
            }
        }

        return netTotal;
    }

    /**
     * Generates the next sequential number based on the last number
     * Quote number format: Q-YYYY-NNNN (e.g. Q-2025-0001)
     * @param lastNumber The last issued quote number (e.g. Q-2025-0004)
     * @return The next quote number in sequence (e.g. Q-2025-0005)
     */
    private String generateNextQuoteNumber(String lastNumber) {
        String[] parts = lastNumber.split("-");
        int year = Integer.parseInt(parts[1]);
        int sequence = Integer.parseInt(parts[2]);
        int nextSequence = sequence + 1;

        return String.format("Q-%d-%04d", year, nextSequence);
    }
}
