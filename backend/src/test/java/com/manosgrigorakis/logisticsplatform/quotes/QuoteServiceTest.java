package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import com.manosgrigorakis.logisticsplatform.companyprofile.service.CompanyProfileService;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.generators.QuotePdfGenerator;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.mapper.QuoteItemMapper;
import com.manosgrigorakis.logisticsplatform.quotes.mapper.QuoteMapper;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.quotes.service.QuoteCalculator;
import com.manosgrigorakis.logisticsplatform.quotes.service.QuoteServiceImpl;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {
    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuditService auditService;

    @Mock
    private QuoteCalculator quoteCalculator;

    @Mock
    private DocumentNumberGenerator documentNumberGenerator;

    @Mock
    private QuotePdfGenerator quotePdfGenerator;

    @Mock
    private QuoteMapper quoteMapper;

    @Mock
    private QuoteItemMapper quoteItemMapper;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private static final String BUCKET_PATH = "quotes/";

    private static final String PRESIGNED_URL = "https://minio.example.com/presigned-url";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(quoteService, "bucketPathQuotes", BUCKET_PATH);
    }

    @Test
    void getQuoteById_shouldReturnQuote_whenExists() {
        // Arrange
        String number = "Q-2026-0001";
        Quote quote = new Quote();
        quote.setNumber(number);

        QuoteResponseDTO expectedResponse = new QuoteResponseDTO();
        expectedResponse.setNumber(number);

        String bucketPath = BUCKET_PATH + number;

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(quote));
        when(fileStorageService.createPresignedUrl(bucketPath)).thenReturn(PRESIGNED_URL);
        when(quoteMapper.toResponse(quote)).thenReturn(expectedResponse);

        // Act
        QuoteResponseDTO response = quoteService.getQuoteById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(quote.getNumber(), response.getNumber());
        assertEquals(PRESIGNED_URL, response.getPdfUrl());
        verify(quoteRepository, times(1)).findById(1L);
        verify(fileStorageService, times(1)).createPresignedUrl(bucketPath);
    }

    @Test
    void getQuoteById_shouldThrowNotFoundException_whenNotExists() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> quoteService.getQuoteById(1L));
        verify(quoteRepository, times(1)).findById(1L);
        verify(fileStorageService, never()).createPresignedUrl(BUCKET_PATH);
    }

    @Test
    void createQuote_shouldCreateQuote() {
        // Arrange
        Quote expectedQuote = new Quote();
        User mockUser = new User();
        Customer mockCustomer = Customer.builder().firstName("John").lastName("Doe").build();
        QuoteRequestDTO request = new QuoteRequestDTO();
        QuoteItemRequestDTO mockItemRequest = new QuoteItemRequestDTO();

        expectedQuote.setCustomer(mockCustomer);
        mockItemRequest.setPrice(new BigDecimal("10.00"));
        mockItemRequest.setQuantity(2);
        request.setQuoteItems(List.of(mockItemRequest));
        request.setUserId(1L);
        request.setCustomerId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(quoteMapper.toEntity(request, mockUser, mockCustomer)).thenReturn(expectedQuote);
        when(quoteRepository.findLastQuoteNumberByYear(LocalDate.now().getYear())).thenReturn(
                Optional.of("Q-2026-0001"));
        when(documentNumberGenerator.generateNextSequentialNumber(eq("Q"), anyString())).thenReturn("Q-2026-0001");
        mockCompanyProfile();
        when(quoteCalculator.calculateNetTotal(expectedQuote)).thenReturn(new BigDecimal("20.00"));
        when(quotePdfGenerator.generatePdf(any())).thenReturn(new byte[0]);
        when(quoteRepository.save(expectedQuote)).thenReturn(expectedQuote);
        when(fileStorageService.createPresignedUrl(anyString())).thenReturn(PRESIGNED_URL);
        when(quoteMapper.toCreatedResponse(expectedQuote)).thenReturn(new QuoteCreatedResponseDTO());

        // Act
        quoteService.createQuote(request);

        // Assert
        ArgumentCaptor<Quote> captor = ArgumentCaptor.forClass(Quote.class);
        verify(quoteRepository).save(captor.capture());

        Quote savedQuote = captor.getValue();
        assertEquals(QuoteStatus.DRAFT, savedQuote.getQuoteStatus());
        assertEquals(0, savedQuote.getNetPrice().compareTo(new BigDecimal("20.00")));
        assertEquals(0, savedQuote.getVatAmount().compareTo(new BigDecimal("4.80")));
        assertEquals(0, savedQuote.getGrossPrice().compareTo(new BigDecimal("24.80")));
    }

    @Test
    void createQuote_shouldThrowResourceNotFound_whenUserNotExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        QuoteRequestDTO request = new QuoteRequestDTO();
        request.setUserId(1L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> quoteService.createQuote(request));
        verify(userRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
        verify(fileStorageService, never()).createPresignedUrl(anyString());
    }

    @Test
    void createQuote_shouldThrowResourceNotFound_whenCustomerNotExists() {
        // Arrange
        User mockUser = new User();
        QuoteRequestDTO request = new QuoteRequestDTO();
        request.setCustomerId(1L);
        request.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> quoteService.createQuote(request));
        verify(userRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
        verify(fileStorageService, never()).createPresignedUrl(anyString());
    }

    @Test
    void updateQuote_shouldUpdateQuote() {
        // Arrange
        Quote mockQuote = new Quote();
        Customer mockCustomer = new Customer();
        QuoteUpdateRequestDTO request = new QuoteUpdateRequestDTO();
        QuoteItemRequestDTO mockItemRequest = new QuoteItemRequestDTO();
        BigDecimal netPrice = new BigDecimal("20.00");

        mockQuote.setQuoteStatus(QuoteStatus.DRAFT);
        mockQuote.setCustomer(mockCustomer);
        mockItemRequest.setPrice(new BigDecimal("10.00"));
        mockItemRequest.setQuantity(2);

        request.setQuoteItems(List.of(mockItemRequest));
        request.setCustomerId(1L);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(mockQuote));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(quoteItemMapper.toEntity(mockItemRequest)).thenReturn(new QuoteItem());
        mockCompanyProfile();
        when(quoteCalculator.calculateNetTotal(mockQuote)).thenReturn(netPrice);
        when(quotePdfGenerator.generatePdf(any())).thenReturn(new byte[0]);
        when(quoteRepository.save(mockQuote)).thenReturn(mockQuote);
        when(fileStorageService.createPresignedUrl(anyString())).thenReturn(PRESIGNED_URL);
        when(quoteMapper.toResponse(mockQuote)).thenReturn(new QuoteResponseDTO());

        // Act
        QuoteResponseDTO response = quoteService.updateQuote(1L, request);

        // Assert
        verify(quoteMapper, times(1)).toUpdate(eq(mockQuote), eq(request), any(BigDecimal.class), any(BigDecimal.class),
                                               any(BigDecimal.class), eq(mockCustomer));
        verify(quoteRepository, times(1)).save(mockQuote);
    }

    @Test
    void updateQuote_shouldThrow_whenQuoteNotExists() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> quoteService.updateQuote(1L, new QuoteUpdateRequestDTO()));
        verify(quoteRepository, times(1)).findById(1L);
        verify(customerRepository, never()).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
        verify(fileStorageService, never()).createPresignedUrl(anyString());
    }

    @Test
    void updateQuote_shouldThrowConflictException_whenQuoteStatusIsNotEditable() {
        // Arrange
        Customer mockCustomer = new Customer();
        Quote mockQuote = Quote.builder().quoteStatus(QuoteStatus.EXPIRED).customer(mockCustomer).build();

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(mockQuote));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        QuoteUpdateRequestDTO request = new QuoteUpdateRequestDTO();
        request.setCustomerId(1L);

        // Act && Assert
        assertThrows(ConflictException.class, () -> quoteService.updateQuote(1L, request));
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    void updateQuoteStatus_shouldUpdateQuoteStatus() {
        // Assert
        Quote mockQuote = new Quote();
        Customer mockCustomer = new Customer();
        mockQuote.setQuoteStatus(QuoteStatus.DRAFT);
        mockQuote.setCustomer(mockCustomer);

        UpdateQuoteStatusRequestDTO request = new UpdateQuoteStatusRequestDTO();
        request.setQuoteStatus(QuoteStatus.SENT);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(mockQuote));

        // Act
        quoteService.updateQuoteStatus(1L, request);

        // Assert
        assertEquals(QuoteStatus.SENT, mockQuote.getQuoteStatus());
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).save(mockQuote);
    }

    @Test
    void updateQuoteStatus_shouldThrowResourceNotFoundException_whenQuoteNotExists() {
        // Arrange
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                quoteService.updateQuoteStatus(1L, new UpdateQuoteStatusRequestDTO()));
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    void updateQuoteStatus_shouldThrowConflictException_whenInvalidStatusTransition() {
        // Assert
        Quote mockQuote = new Quote();
        mockQuote.setQuoteStatus(QuoteStatus.DRAFT);

        UpdateQuoteStatusRequestDTO request = new UpdateQuoteStatusRequestDTO();
        request.setQuoteStatus(QuoteStatus.ACCEPTED);

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(mockQuote));

        // Act & Assert
        assertThrows(ConflictException.class, () -> quoteService.updateQuoteStatus(1L, request));
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(mockQuote);
    }

    private void mockCompanyProfile() {
        when(companyProfileService.getCompanyProfileEntity()).thenReturn(
                CompanyProfile.builder()
                        .name("Test Company")
                        .slogan("Test")
                        .email("test@test.com")
                        .street("Street")
                        .streetNumber("1")
                        .postalCode("12345")
                        .region("Athens")
                        .tin("123456789")
                        .vatPercentage(24)
                        .representative("John Doe")
                        .representativeTitle("Manager")
                        .brandPrimaryColor("#0f172a")
                        .brandSecondaryColor("#2563eb")
                        .phones(List.of("2101234567"))
                        .country("Greece")
                        .build());
    }
}
