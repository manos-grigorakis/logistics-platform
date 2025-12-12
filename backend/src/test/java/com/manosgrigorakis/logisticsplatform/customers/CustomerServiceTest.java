package com.manosgrigorakis.logisticsplatform.customers;

import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.customers.service.CustomerServiceImpl;
import com.manosgrigorakis.logisticsplatform.infrastructure.mail.MailService;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class CustomerServiceTest {
    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @MockitoBean
    private MailService mailService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl customerService;

    @Test
    void getCustomer_shouldReturnCustomer() {
        // Assert
        Customer customer = createAndSaveCustomer("123456789", "Papadopoulos");

        // Act
        CustomerResponseDTO foundedCustomer = customerService.getCustomerById(customer.getId());

        // Assert
        assertEquals(customer.getTin(), foundedCustomer.getTin());
        assertEquals(customer.getCompanyName(), foundedCustomer.getCompanyName());
    }

    @Test
    void getCustomer_shouldThrow_whenCustomerNotFound() {
        // Assert & Act
        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(9999L));
    }

    @Test
    void createCustomer_shouldCreateCustomer() {
        // Arrange
        CustomerRequestDTO dto = createCustomerDTO("123456789", "Cargo A.E.");

        // Act
        CustomerResponseDTO response = customerService.createCustomer(dto);

        // Assert
        assertEquals("123456789", response.getTin());
        assertEquals("Cargo A.E.", response.getCompanyName());
        assertEquals("John",response.getFirstName());
        assertEquals("Doe",response.getLastName());
        assertEquals(CustomerType.COMPANY, response.getCustomerType());
    }

    @Test
    void createCustomer_shouldThrow_whenTinExists() {
        // Arrange
        createAndSaveCustomer("123456789", "Papadopoulos"); // Existing customer
        CustomerRequestDTO dto = createCustomerDTO("123456789", "Cargo A.E.");

        // Act
        assertThrows(DuplicateEntryException.class,
                () -> customerService.createCustomer(dto));
    }

    @Test
    void createCustomer_shouldThrow_whenCompanyNameExists() {
        // Arrange
        createAndSaveCustomer("987654321", "Papadopoulos"); // Existing customer
        CustomerRequestDTO dto = createCustomerDTO("123456789", "Papadopoulos");

        // Assert & Act
        assertThrows(DuplicateEntryException.class,
                () -> customerService.createCustomer(dto));
    }

    @Test
    void updateCustomer_shouldUpdateCustomer() {
        // Arrange
        Customer existingCustomer = createAndSaveCustomer("987654321", "Papadopoulos");
        UpdateCustomerRequestDTO dto = updateCustomerDTO( "Cargo A.E.");

        // Act
        CustomerResponseDTO response = customerService.updateCustomerById(existingCustomer.getId(), dto);

        // Assert
        assertEquals(existingCustomer.getId(), response.getId());
        assertEquals("987654321", response.getTin());
        assertEquals("Cargo A.E.", response.getCompanyName());
    }

    @Test
    void updateCustomer_shouldThrow_whenCompanyNameExists() {
        // Arrange
        createAndSaveCustomer("123456789", "Papadopoulos"); // Existing customer
        Customer customer = createAndSaveCustomer("987654321", "Cargo A.E.");
        UpdateCustomerRequestDTO dto = updateCustomerDTO( "Papadopoulos");

        assertThrows(DuplicateEntryException.class,
                () -> customerService.updateCustomerById(customer.getId(), dto));
    }

    private Customer createAndSaveCustomer(String tin, String companyName) {
        Customer customer = Customer.builder()
                .tin(tin)
                .companyName(companyName)
                .firstName("Maria")
                .lastName("Papadopoulou")
                .customerType(CustomerType.COMPANY)
                .build();

        return customerRepository.save(customer);
    }

    private CustomerRequestDTO createCustomerDTO(String tin, String companyName) {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setTin(tin);
        dto.setCompanyName(companyName);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setCustomerType(CustomerType.COMPANY);
        return dto;
    }

    private UpdateCustomerRequestDTO updateCustomerDTO(String companyName) {
        UpdateCustomerRequestDTO dto = new UpdateCustomerRequestDTO();
        dto.setCompanyName(companyName);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setCustomerType(CustomerType.COMPANY);
        return dto;
    }
}
