package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteCreatedResponseDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteResponseDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteUpdateRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteItemUnit;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.quotes.service.QuoteServiceImpl;
import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class QuoteServiceTest {
    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private QuoteServiceImpl quoteService;

    @Test
    void getQuoteById_shouldReturnQuote() {
        // Arrange
        QuoteRequestDTO dto = this.createQuote();
        QuoteCreatedResponseDTO responseDTO = quoteService.createQuote(dto);

        // Act
        QuoteResponseDTO response = quoteService.getQuoteById(responseDTO.getId());

        // Assert
        assertEquals(responseDTO.getId(), response.getId());
        assertEquals(0, response.getGrossPrice().compareTo(responseDTO.getGrossPrice()));
    }

    @Test
    void createQuote_shouldCreateQuote() {
        // Arrange
        LocalDate today = LocalDate.now();
        QuoteRequestDTO dto = this.createQuote();

        // Act
        QuoteCreatedResponseDTO responseDTO = quoteService.createQuote(dto);
        Quote foundedQuote = quoteRepository.findById(responseDTO.getId())
                .orElseThrow();

        // Assert
        assertEquals(dto.getOrigin(), foundedQuote.getOrigin());
        assertEquals(dto.getDestination(), foundedQuote.getDestination());

        assertEquals(today, foundedQuote.getIssueDate());
        assertEquals(QuoteStatus.DRAFT, foundedQuote.getQuoteStatus());
        assertEquals(today.plusDays(30), foundedQuote.getExpirationDate());

        assertEquals(0, foundedQuote.getNetPrice().compareTo(new BigDecimal("100.00")));
        assertEquals(0, foundedQuote.getVatAmount().compareTo(new BigDecimal("24.00")));
        assertEquals(0, foundedQuote.getGrossPrice().compareTo(new BigDecimal("124.00")));

        assertEquals(1, foundedQuote.getQuoteItems().size());
        assertEquals("Boat", foundedQuote.getQuoteItems().get(0).getName());
    }

    @Test
    void updateQuote_shouldUpdateQuote() {
        // Arrange
        QuoteRequestDTO dto = this.createQuote();
        QuoteCreatedResponseDTO responseDTO = quoteService.createQuote(dto);
        Quote foundedQuote = quoteRepository.findById(responseDTO.getId())
                .orElseThrow();

        QuoteItemRequestDTO itemRequestDTO1 = createItem("Boat", 1, new BigDecimal("100.00"));
        QuoteItemRequestDTO itemRequestDTO2 = createItem("Box", 1, new BigDecimal("20.00"));

        QuoteUpdateRequestDTO requestDTO = new QuoteUpdateRequestDTO();
        requestDTO.setValidityDays(30);
        requestDTO.setOrigin("Athens");
        requestDTO.setDestination("Patra");
        requestDTO.setCustomerId(foundedQuote.getCustomer().getId());
        requestDTO.setQuoteItems(List.of(itemRequestDTO1, itemRequestDTO2));

        // Act
        QuoteResponseDTO response = quoteService.updateQuote(foundedQuote.getId(), requestDTO);

        // Assert
        assertEquals("Athens", response.getOrigin());
        assertEquals("Patra", response.getDestination());

        assertEquals(0, response.getNetPrice().compareTo(new BigDecimal("120.00")));
        assertEquals(0, response.getVatAmount().compareTo(new BigDecimal("28.80")));
        assertEquals(0, response.getGrossPrice().compareTo(new BigDecimal("148.80")));

        assertEquals(2, response.getQuoteItems().size());
        assertEquals("Boat", response.getQuoteItems().get(0).getName());
        assertEquals("Box", response.getQuoteItems().get(1).getName());

    }

    // Helpers
    private QuoteRequestDTO createQuote() {
        QuoteItemRequestDTO itemRequestDTO = createItem("Boat", 1, new BigDecimal("100.00"));

        QuoteRequestDTO dto = new QuoteRequestDTO();
        dto.setOrigin("Athens");
        dto.setDestination("Karditsa");
        dto.setValidityDays(30);
        dto.setUserId(createUser().getId());
        dto.setCustomerId(createCustomer().getId());
        dto.setQuoteItems(List.of(itemRequestDTO));

        return dto;
    }

    private QuoteItemRequestDTO createItem(String name, int quantity, BigDecimal price) {
        QuoteItemRequestDTO itemRequestDTO = new QuoteItemRequestDTO();
        itemRequestDTO.setName(name);
        itemRequestDTO.setQuantity(quantity);
        itemRequestDTO.setUnit(QuoteItemUnit.PIECE);
        itemRequestDTO.setPrice(price);
        return itemRequestDTO;
    }

    private User createUser() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .status(UserStatus.INVITED)
                .build();

        return userRepository.save(user);
    }

    private Customer createCustomer() {
        Customer customer = Customer.builder()
                .tin("123456789")
                .companyName("Cargo A.E.")
                .firstName("Maria")
                .lastName("Papadopoulou")
                .customerType(CustomerType.COMPANY)
                .build();

        return customerRepository.save(customer);
    }
}
