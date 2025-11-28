package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<CustomerResponseDTO> getAllCustomers() {return customerService.getAllCustomers();}

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping()
    public CustomerResponseDTO createUser(@RequestBody @Valid CustomerRequestDTO dto) {
        return customerService.createCustomer(dto);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO updateCustomerById(@PathVariable Long id, @RequestBody @Valid UpdateCustomerRequestDTO dto) {
        return customerService.updateCustomerById(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);

        return ResponseEntity.noContent().build();
    }
}
