package com.manosgrigorakis.logisticsplatform.suppliers.service.impl;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierListResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.mapper.SupplierMapper;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import com.manosgrigorakis.logisticsplatform.suppliers.repository.SupplierRepository;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public Page<SupplierListResponse> findAllSuppliers(SupplierFilterRequest filterRequest, PageFilterRequest page,
                                                   SortFilterRequest sort) {
        Pageable pageable = PageRequest.of(page.getPage(), page.getSize(), sort.createSort());
        Page<SupplierListResponse> supplierPage;

        if (filterRequest.companyName() != null && !filterRequest.companyName().isBlank()) {
            supplierPage = supplierRepository.findSupplierWithTotals(filterRequest.companyName(), pageable);
        } else {
            supplierPage = supplierRepository.findSupplierWithTotals(null, pageable);
        }

        return supplierPage;
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier not found with id {}", id);
            return new ResourceNotFoundException("Supplier not found with id " + id);
        });

        return SupplierMapper.toResponse(supplier);
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        if(supplierRepository.existsByCompanyName(request.companyName())) {
            log.warn("Supplier already exists with company name {}", request.companyName());
            throw new DuplicateEntryException("Supplier with company name already exists", "COMPANY_NAME");
        }

        Supplier supplier = SupplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);

        log.info("Created Supplier with id {}", savedSupplier.getId());
        return SupplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponse updateSupplierById(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier not found with id {}", id);
            return new ResourceNotFoundException("Supplier not found with id " + id);
        });

        supplierRepository.findByCompanyName(request.companyName())
                .filter(existing -> !existing.getId().equals(supplier.getId()))
                .ifPresent(existing -> {
                    log.warn("Supplier already exists with company name {}", request.companyName());
                    throw new DuplicateEntryException("Supplier with company name already exists", "COMPANY_NAME");
                });

        supplier.setCompanyName(request.companyName());
        supplier.setEmail(request.email());
        Supplier updatedSupplier = supplierRepository.save(supplier);

        log.info("Updated Supplier with id {}", updatedSupplier.getId());
        return SupplierMapper.toResponse(updatedSupplier);
    }

    @Override
    public void deactivateSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier not found with id {}", id);
            return new ResourceNotFoundException("Supplier not found with id " + id);
        });

        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
}
