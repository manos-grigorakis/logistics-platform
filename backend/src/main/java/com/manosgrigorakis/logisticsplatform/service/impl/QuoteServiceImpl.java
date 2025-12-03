package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteResponseDTO;
import com.manosgrigorakis.logisticsplatform.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.mapper.QuoteMapper;
import com.manosgrigorakis.logisticsplatform.model.Customer;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);

    @Value("${tax.vat}")
    private Integer vatPercent;

    public QuoteServiceImpl(QuoteRepository quoteRepository, UserRepository userRepository,
                            CustomerRepository customerRepository) {
        this.quoteRepository = quoteRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<QuoteResponseDTO> getAllQuotes() {
        return null;
    }

    @Override
    public QuoteResponseDTO getQuoteById(Long id) {
        return null;
    }

    @Override
    public QuoteResponseDTO createQuote(QuoteRequestDTO dto) {
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

        int currentYear = LocalDate.now().getYear();
        String lastNumber = quoteRepository.findLastQuoteNumberByYear(currentYear)
                        .orElse("Q-" + currentYear + "-0000");

        String newNumber = generateNextQuoteNumber(lastNumber);
        quote.setNumber(newNumber);

        BigDecimal netTotal = calculateNetTotal(quote);
        BigDecimal vatAmount = calculateVatAmount(netTotal);
        BigDecimal grossTotal = calculateGrossTotal(netTotal, vatAmount);

        quote.setTaxRatePercentage(vatPercent);
        quote.setNetPrice(netTotal.setScale(2, RoundingMode.HALF_UP));
        quote.setVatAmount(vatAmount.setScale(2, RoundingMode.HALF_UP));
        quote.setGrossPrice(grossTotal.setScale(2, RoundingMode.HALF_UP));
        Quote savedQuote = quoteRepository.save(quote);

        log.info("Quote created with id: {}", quote.getId());
        return QuoteMapper.toResponse(savedQuote);
    }


    @Override
    public QuoteResponseDTO updateQuote(Long id, QuoteRequestDTO dto) {
        return null;
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
     * Calculates the VAT amount based on net total,
     * using the configured tax rate
     * @param netTotal Amount before tax
     * @return Calculated VAT amount
     */
    private BigDecimal calculateVatAmount(BigDecimal netTotal) {
        return netTotal.multiply(getVatRate());
    }

    /**
     * Calculates the gross total of a Quote
     * The method sums the net total with vat amount
     * @param netTotal Amount before tax
     * @param vatAmount Calculated tax
     * @return gross total
     */
    private BigDecimal calculateGrossTotal(BigDecimal netTotal, BigDecimal vatAmount) {
        return netTotal.add(vatAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Converts VAT percentage to VAT rate
     * @return The factor of vatPercent (e.g. 24 -> 0.24)
     */
    private BigDecimal getVatRate() {
        return BigDecimal.valueOf(vatPercent)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
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
