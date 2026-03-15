package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ExcelInvoiceImportResultDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ExcelInvoiceReader;
import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.mapper.InvoiceMapper;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    private final ExcelInvoiceReader excelInvoiceReader;

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository,
            ExcelInvoiceReader excelInvoiceReader
    ) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.excelInvoiceReader = excelInvoiceReader;
    }

    @Override
    public BulkInvoiceResponseDTO bulkInvoicesImport(BulkInvoiceRequestDTO dto) {
        String fileName = dto.getFile().getOriginalFilename();

        if(fileName == null || !fileName.endsWith(".xlsx")) {
            throw new BadRequestException("Only .xlsx files are support", "UNSUPPORTED_FILE");
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Customer not found with id: {}", dto.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId());
                });

        ExcelInvoiceImportResultDTO data;

        try {
            data = excelInvoiceReader.readExcel(dto.getFile());
        } catch (IOException e) {
            log.error(
                    "Failed to read Excel file during bulk invoice import. File name: {}",
                    dto.getFile().getOriginalFilename()
            );
            throw new BadRequestException("Failed to process uploaded Excel file", "EXCEL_FILE_PROCESSING_FAILED");
        }

        if (!Objects.equals(customer.getTin(), data.tin())) {
            log.warn(
                    "TIN mismatch during bulk invoices import. Expected customer TIN: {}, Excel file TIN: {}",
                    customer.getTin(), data.tin()
            );

            throw new ConflictException(
                    "Customer TIN doesn't match the TIN founded in the uploaded Excel file",
                    "CUSTOMER_TIN_MISMATCH",
                    Map.of(
                            "expectedTin", customer.getTin(),
                            "fileTin", data.tin()
                    )
            );
        }

        List<Invoice> invoices = data.invoices().stream().map(InvoiceMapper::toEntity).toList();
        List<Invoice> filteredInvoices = invoices.stream()
                .filter(i -> !this.invoiceRepository.existsByExternalInvoiceNumber(i.getExternalInvoiceNumber()))
                .toList();

        filteredInvoices.forEach(i -> {
            i.setCustomer(customer);
            i.setRemainingAmount(BigDecimal.ZERO);
            i.setStatus(InvoiceStatus.OUTSTANDING);
        });

        List<Invoice> savedInvoices = this.invoiceRepository.saveAll(filteredInvoices);
        log.info("Bulk invoice import successful for customer id: {}", customer.getId());

        int skippedInvoices = invoices.size() - savedInvoices.size();

        return new BulkInvoiceResponseDTO(
                invoices.size(),
                savedInvoices.size(),
                skippedInvoices
        );
    }
}
