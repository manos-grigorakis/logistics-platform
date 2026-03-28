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
import com.manosgrigorakis.logisticsplatform.payments.dto.PrepareReconciliationResult;
import com.manosgrigorakis.logisticsplatform.payments.dto.ProcessingInvoicesBulkImportResponse;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.mapper.InvoiceMapper;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        ProcessingInvoicesBulkImportResponse response =
                this.prepareInvoicesFromImport(dto.getFile(), dto.getCustomerId());

        List<Invoice> savedInvoices = this.invoiceRepository.saveAll(response.invoices());
        log.info("Bulk invoice import successful for customer id: {}", response.customer().getId());

        int skippedInvoices = response.originalInvoicesLength() - savedInvoices.size();

        return new BulkInvoiceResponseDTO(
                response.originalInvoicesLength(),
                savedInvoices.size(),
                skippedInvoices
        );
    }

    /**
     * Prepares invoices from an imported Excel file so they can be used
     * during the reconciliation process
     * @param customerId The {@link Customer} id
     * @param file The imported Excel file
     * @return A list of prepared {@link Invoice} entities
     * @throw {@link BadRequestException} when no invoices found in the uploaded file or invoices already exist
     */
    @Override
    public PrepareReconciliationResult prepareInvoicesForReconciliation(Long customerId, MultipartFile file) {
        ProcessingInvoicesBulkImportResponse response = this.prepareInvoicesFromImport(file, customerId);
        
        if (response.originalInvoicesLength() == 0) {
            log.warn("No invoices found in the uploaded file for customer with id: {}", customerId);
            throw new BadRequestException("No invoices found in the uploaded file", "NO_INVOICES_FOUND");
        }

        if(response.invoices().isEmpty()) {
            log.warn("All {} invoices already exist in the system for customer with id: {}",
                     response.originalInvoicesLength(), customerId);
            throw new BadRequestException("All invoices already exist in the system", "INVOICES_ALREADY_EXIST");
        }

        return new PrepareReconciliationResult(response.customer(), response.invoices());
    }

    /**
     * Reads and processes an imported Excel fie containing invoices and prepares them for further processing. <br>
     * <b>Steps in Method</b>:
     * <ul>
     *     <li>Validation of file type</li>
     *     <li>Load the customer</li>
     *     <li>Parse the imported Excel file</li>
     *     <li>Validate Customer TIN</li>
     *     <li>Map Invoices to entities</li>
     *     <li>Filter invoices that already exist in the database</li>
     *     <li>Prepare Invoices with default status and amounts</li>
     * </ul>
     * @param file The uploaded Excel file
     * @param customerId The {@link Customer} id
     * @return A {@link  ProcessingInvoicesBulkImportResponse} containing the prepared invoices
     * @throw {@link ResourceNotFoundException} when customer not found by {@code id}
     */
    private ProcessingInvoicesBulkImportResponse prepareInvoicesFromImport(MultipartFile file, Long customerId) {
        this.validateFileType(file);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found with id: {}", customerId);
                    return new ResourceNotFoundException("Customer not found with id: " + customerId);
                });

        ExcelInvoiceImportResultDTO data = this.processInvoicesExcelFile(file);
        this.validateCustomerTin(customer.getTin(), data.tin());

        List<Invoice> invoices = data.invoices().stream().map(InvoiceMapper::toEntity).toList();

        List<Invoice> filteredInvoices = invoices.stream()
                .filter(i -> !this.invoiceRepository.existsByExternalInvoiceNumber(i.getExternalInvoiceNumber()))
                .toList();

        filteredInvoices.forEach(i -> {
            i.setCustomer(customer);
            i.setRemainingAmount(i.getTotalAmount());
            i.setStatus(InvoiceStatus.OUTSTANDING);
        });

        return new ProcessingInvoicesBulkImportResponse(filteredInvoices, customer, invoices.size());
    }

    /**
     * Validates that the provided {@code file} has the {@code .xlsx} extension
     * @param file The file which will be validated
     * @throws BadRequestException If the file extension is invalid
     */
    private void validateFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if(fileName == null || !fileName.endsWith(".xlsx")) {
            throw new BadRequestException("Only .xlsx files are support", "UNSUPPORTED_FILE");
        }
    }

    /**
     * Processes the Invoices Excel file using the {@link #excelInvoiceReader} to read its data
     * @param file The Excel file which includes the {@link Invoice}
     * @return {@link  ExcelInvoiceImportResultDTO} The data from the processed Excel file
     * @throws BadRequestException If the process operation fails
     */
    private ExcelInvoiceImportResultDTO processInvoicesExcelFile(MultipartFile file) {
        try {
            return excelInvoiceReader.readExcel(file);
        } catch (IOException e) {
            log.error("Failed to read Excel file during bulk invoice import. File name: {}",
                    file.getOriginalFilename(), e
            );
            throw new BadRequestException("Failed to process uploaded Excel file", "EXCEL_FILE_PROCESSING_FAILED");
        }
    }

    /**
     * Validates the {@link  Customer} TIN from the request if its match with the TIN,
     * from the imported file
     * @param customerTin The {@link Customer} TIN number
     * @param fileTin The TIN number in the imported file
     * @throws ConflictException If there is a miss match in the validation
     */
    private void validateCustomerTin(String customerTin, String fileTin) {
        if (!Objects.equals(customerTin, fileTin)) {
            log.warn(
                    "TIN mismatch during bulk invoices import. Expected customer TIN: {}, Excel file TIN: {}",
                    customerTin, fileTin
            );

            throw new ConflictException(
                    "Customer TIN doesn't match the TIN founded in the uploaded Excel file",
                    "CUSTOMER_TIN_MISMATCH",
                    Map.of(
                            "expectedTin", customerTin,
                            "fileTin", fileTin
                    )
            );
        }
    }
}
