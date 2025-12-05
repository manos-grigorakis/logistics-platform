package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.filters.CustomerFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "CRUD operation for customers")
public class CustomerRestController {
    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Get all Customers", description = "Lists all customers with pagination")
    @ApiResponse(responseCode = "200", description = "List of customers with pagination")
    @GetMapping()
    public Page<CustomerResponseDTO> getAllCustomers(
            @ParameterObject @ModelAttribute @Valid CustomerFilterRequest customerFilter,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest page,
            @ParameterObject @ModelAttribute SortFilterRequest sort) {
        return customerService.getAllCustomers(customerFilter, page, sort);
    }

    @Operation(summary = "Get Customer by Id", description = "Find customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded customer"),
            @ApiResponse(responseCode = "404", description = "Customer doesn't exist"),
    })
    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @Operation(summary = "Create a Customer", description = "Creates a new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer created successfully"),
            @ApiResponse(responseCode = "409", description = "TIN already exists | Company name already exists"),
    })
    @PostMapping()
    public CustomerResponseDTO createCustomer(@RequestBody @Valid CustomerRequestDTO dto) {
        return customerService.createCustomer(dto);
    }

    @Operation(summary = "Update Customer by Id", description = "Update customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Company name already exists"),
    })
    @PutMapping("/{id}")
    public CustomerResponseDTO updateCustomerById(@PathVariable Long id, @RequestBody @Valid UpdateCustomerRequestDTO dto) {
        return customerService.updateCustomerById(id, dto);
    }

    @Operation(summary = "Delete Customer by Id", description = "Delete customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer doesn't exist"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);

        return ResponseEntity.noContent().build();
    }
}
