package com.manosgrigorakis.logisticsplatform.customers.service;

import com.manosgrigorakis.logisticsplatform.customers.dto.*;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.customers.mapper.CustomerMapper;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.customers.specs.CustomerSpecs;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final QuoteRepository quoteRepository;

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(CustomerRepository customerRepository, QuoteRepository quoteRepository) {
        this.customerRepository = customerRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public Page<CustomerResponseDTO> getAllCustomers(CustomerFilterRequest customerFilter,
                                                     PageFilterRequest page, SortFilterRequest sort) {
        Specification<Customer> spec = Specification.allOf();

        if(customerFilter.getTin() != null) {
            spec = spec.and(CustomerSpecs.likeTin(customerFilter.getTin()));
        }

        if(customerFilter.getCompanyName() != null) {
            spec = spec.and(CustomerSpecs.likeCompanyName(customerFilter.getCompanyName()));
        }

        if(customerFilter.getCustomerType() != null) {
            spec = spec.and(CustomerSpecs.equalCustomerType(customerFilter.getCustomerType()));
        }

        Pageable pageable = PageRequest.of(page.getPage(), page.getSize(), sort.createSort());

        Page<Customer> customerPage = customerRepository.findAll(spec, pageable);

        return customerPage.map(CustomerMapper::toResponse);
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
        Customer customer = CustomerMapper.toEntity(dto);

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

        Optional<Customer> existingCustomerCompanyName = customerRepository.findByCompanyName(dto.getCompanyName());
        Optional<Customer> existing = customerRepository.findByCompanyName(dto.getCompanyName());

        if (existingCustomerCompanyName.isPresent() && !existing.get().getId().equals(customer.getId())) {
            log.warn("Update failed. Attempted to update customer with existing company name: {}", dto.getCompanyName());
            throw  new DuplicateEntryException("companyName", dto.getCompanyName());
        }

        customer.setTin(customer.getTin());
        customer.setCompanyName(dto.getCompanyName());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setCustomerType(dto.getCustomerType());
        customer.setLocation(dto.getLocation());
        customer.setPhone(dto.getPhone());

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

    @Override
    public Page<QuoteSummaryDTO> quotesPerCustomer(PageFilterRequest page, Long id) {
        Pageable pageable = PageRequest.of(page.getPage(), page.getSize());

        Page<Quote> quotesPage = quoteRepository.findByCustomerId(id, pageable);

        return quotesPage.map(quote ->
                new QuoteSummaryDTO(
                        quote.getId(),
                        quote.getNumber(),
                        quote.getGrossPrice(),
                        quote.getQuoteStatus(),
                        quote.getIssueDate())
        );
    }
}
