package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierListResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierResponse;
import org.springframework.data.domain.Page;

public interface SupplierService {
    Page<SupplierListResponse> findAllSuppliers(SupplierFilterRequest filterRequest, PageFilterRequest page,
                                                SortFilterRequest sort);

    SupplierResponse getSupplierById(Long id);

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplierById(Long id, SupplierRequest request);

    void deleteSupplierById(Long id);
}
