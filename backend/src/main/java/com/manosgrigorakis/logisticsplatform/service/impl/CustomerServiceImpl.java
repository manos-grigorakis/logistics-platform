package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.mapper.CustomerMapper;
import com.manosgrigorakis.logisticsplatform.model.Customer;
import com.manosgrigorakis.logisticsplatform.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return List.of();
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        Customer customer = Customer.builder()
                .tin(dto.getTin())
                .companyName(dto.getCompanyName())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .customerType(dto.getCustomerType())
                .location(dto.getLocation())
                .phone(dto.getPhone())
                .build();


        Optional<Customer> existingCustomerByTin = customerRepository.findByTin(dto.getTin());
        Optional<Customer> existingCustomerCompanyName = customerRepository.findByCompanyName(dto.getCompanyName());

        if (existingCustomerByTin.isPresent()) {
           log.warn("Attempted to create duplicate customer with TIN: {}", dto.getTin());
           throw  new DuplicateEntryException("tin", dto.getTin());
        }

        if (existingCustomerCompanyName.isPresent()) {
            log.warn("Attempted to create duplicate customer with company name: {}", dto.getCompanyName());
            throw  new DuplicateEntryException("companyName", dto.getCompanyName());
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with company name: {}", dto.getCompanyName());

        return CustomerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomerById(Long id, UpdateCustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Updated failed. Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer not found with id: " + id);
                });

        customer.setTin(customer.getTin());
        customer.setCompanyName(dto.getCompanyName());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setCustomerType(dto.getCustomerType());
        customer.setLocation(dto.getLocation());
        customer.setPhone(dto.getPhone());

        Optional<Customer> existingCustomerCompanyName = customerRepository.findByCompanyName(dto.getCompanyName());
        Optional<Customer> existing = customerRepository.findByCompanyName(dto.getCompanyName());

        if (existingCustomerCompanyName.isPresent() && !existing.get().getId().equals(customer.getId())) {
            log.warn("Update failed. Attempted to update customer with existing company name: {}", dto.getCompanyName());
            throw  new DuplicateEntryException("companyName", dto.getCompanyName());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated: {}", dto.getCompanyName());

        return CustomerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomerById(Long id) {
        if(!customerRepository.existsById(id)) {
            log.error("Delete failed. Customer not found with id: {}", id);
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
        log.info("Customer deleted: {}", id);
    }
}
