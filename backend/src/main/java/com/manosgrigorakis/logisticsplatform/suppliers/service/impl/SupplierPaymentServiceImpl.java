package com.manosgrigorakis.logisticsplatform.suppliers.service.impl;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.DocumentProcessingException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentCreateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentUpdateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.mapper.SupplierPaymentMapper;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import com.manosgrigorakis.logisticsplatform.suppliers.model.SupplierPayment;
import com.manosgrigorakis.logisticsplatform.suppliers.repository.SupplierPaymentRepository;
import com.manosgrigorakis.logisticsplatform.suppliers.repository.SupplierRepository;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SupplierPaymentServiceImpl implements SupplierPaymentService {
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final SupplierRepository supplierRepository;
    private final DocumentNumberGenerator documentNumberGenerator;
    private final FileStorageService fileStorageService;

    private final String fileCode = "SP";
    private final List<String> allowedContentTypes = List.of("application/pdf", "image/jpeg", "image/png");

    @Value("${app.minio.bucketPathSuppliers}")
    private String bucketPathSuppliers;

    @Override
    public SupplierPaymentResponse getSupplierPaymentById(Long id) {
        SupplierPayment payment = supplierPaymentRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier payment not found with id {}", id);
            return new ResourceNotFoundException("Supplier payment not found with id " + id);
        });

        String invoicePresignedUrl = payment.getInvoiceUrl() != null ?
                fileStorageService.createPresignedUrl(getFileName(payment.getNumber(), "invoice")) : null;

        String receiptPresignedUrl = payment.getReceiptUrl() != null ?
                fileStorageService.createPresignedUrl(getFileName(payment.getNumber(), "receipt")) : null;

        return SupplierPaymentMapper.toResponse(payment, invoicePresignedUrl, receiptPresignedUrl);
    }

    @Override
    public SupplierPaymentResponse createSupplierPayment(SupplierPaymentCreateRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId()).orElseThrow(() -> {
            log.warn("Supplier not found with id {}", request.supplierId());
            return new ResourceNotFoundException("Supplier not found with id " + request.supplierId());
        });

        // Generates the next sequential payment number for the current year
        int currentYear = LocalDate.now().getYear();
        String lastNumber = supplierPaymentRepository.findLastSupplierPaymentByYear(currentYear).orElse(
                fileCode + "-" + currentYear + "-0000");
        String newNumber = documentNumberGenerator.generateNextSequentialNumber(fileCode, lastNumber);

        SupplierPayment payment = SupplierPaymentMapper.toEntity(request, newNumber, supplier);

        // Set payment status based on amount
        if (request.paidAmount() != null) {
            if (request.paidAmount().compareTo(request.totalAmount()) > 0) {
                throw new ConflictException("Supplier payment paid amount is greater than total amount",
                                            "PAID_AMOUNT_EXCEEDS_TOTAL");
            }
            payment.setStatusBasedOnAmounts();
        }

        String invoiceFileName = getFileName(payment.getNumber(), "invoice");
        String receiptFileName = getFileName(payment.getNumber(), "receipt");
        String invoicePresignedUrl;
        String receiptPresignedUrl;

        // Upload & Store Files
        try {
            invoicePresignedUrl = storeFileIfExists(request.invoiceFile(), invoiceFileName);
            receiptPresignedUrl = storeFileIfExists(request.receiptFile(), receiptFileName);

            if (invoicePresignedUrl != null) payment.setInvoiceUrl(invoiceFileName);
            if (receiptPresignedUrl != null) payment.setReceiptUrl(receiptFileName);

            supplierPaymentRepository.save(payment);
            log.info("Supplier payment created with id {}", payment.getNumber());
        } catch (IOException e) {
            log.error("Error while saving files for supplier payment {}", payment.getNumber(), e);
            throw new DocumentProcessingException("Failed to store files", "STORAGE_ERROR");
        } catch (Exception e) {
            log.error("Supplier payment creation failed with number {}", payment.getNumber(), e);
            fileStorageService.deleteObject(invoiceFileName);
            fileStorageService.deleteObject(receiptFileName);
            log.info("Files deleted from storage for supplier payment with number {}", payment.getNumber());
            throw e;
        }

        return SupplierPaymentMapper.toResponse(payment, invoicePresignedUrl, receiptPresignedUrl);
    }

    @Override
    public SupplierPaymentResponse updateSupplierPaymentById(Long id, SupplierPaymentUpdateRequest request) {
        SupplierPayment payment = supplierPaymentRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier payment not found with id {}", id);
            return new ResourceNotFoundException("Supplier payment not found with id " + id);
        });

        String invoiceFileName = getFileName(payment.getNumber(), "invoice");
        String receiptFileName = getFileName(payment.getNumber(), "receipt");
        String invoicePresignedUrl;
        String receiptPresignedUrl;

        // Upload & Store Files
        try {
            invoicePresignedUrl = storeFileIfExists(request.invoiceFile(), invoiceFileName);
            receiptPresignedUrl = storeFileIfExists(request.receiptFile(), receiptFileName);
        } catch (IOException e) {
            log.error("Error while saving files for supplier payment {}", payment.getNumber(), e);
            throw new DocumentProcessingException("Failed to store files", "STORAGE_ERROR");
        }

        if (invoicePresignedUrl != null) {
            payment.setInvoiceUrl(invoiceFileName);
        } else {
            invoicePresignedUrl = payment.getInvoiceUrl() != null ?
                    fileStorageService.createPresignedUrl(payment.getInvoiceUrl()) : null;
        }

        if (receiptPresignedUrl != null) {
            payment.setReceiptUrl(receiptFileName);
        } else {
            receiptPresignedUrl = payment.getReceiptUrl() != null ?
                    fileStorageService.createPresignedUrl(payment.getReceiptUrl()) : null;
        }

        SupplierPayment updatedPayment = SupplierPaymentMapper.toUpdateEntity(payment, request);

        // Set payment status based on amount
        if (request.paidAmount() != null) {
            if (request.paidAmount().compareTo(request.totalAmount()) > 0) {
                throw new ConflictException("Supplier payment paid amount is greater than total amount",
                                            "PAID_AMOUNT_EXCEEDS_TOTAL");
            }
            updatedPayment.setStatusBasedOnAmounts();
        }

        try {
            SupplierPayment savedPayment = supplierPaymentRepository.save(updatedPayment);
            log.info("Supplier payment updated with id {}", updatedPayment.getNumber());

            return SupplierPaymentMapper.toResponse(savedPayment, invoicePresignedUrl, receiptPresignedUrl);
        } catch (Exception e) {
            log.error("Supplier payment update failed with number {}", payment.getNumber(), e);
            throw e;
        }
    }

    @Override
    public void deleteSupplierPaymentById(Long id) {
        SupplierPayment payment = supplierPaymentRepository.findById(id).orElseThrow(() -> {
            log.warn("Supplier payment not found with id {}", id);
            return new ResourceNotFoundException("Supplier payment not found with id " + id);
        });

        supplierPaymentRepository.delete(payment);
    }

    /**
     * Builds the storage file name based on the parameters
     *
     * <p>Uses the {@link #bucketPathSuppliers} as the storage path</p>
     *
     * @param paymentNumber The payment number to used in the storage file name
     * @param fileType      The file type (e.g. {@code invoice} or {@code receipt})
     * @return The generated storage file name
     */
    private String getFileName(String paymentNumber, String fileType) {
        return this.bucketPathSuppliers + paymentNumber + "-" + fileType;
    }

    /**
     * Validates whether the provided content type is allowed
     *
     * @param contentType The content type to validate
     * @return {@code true} If the provided content type is allowed, otherwise {@code false}
     */
    private boolean isAllowedContentType(String contentType) {
        return allowedContentTypes.contains(contentType);
    }

    /**
     * Uploads and stores the provided file in the object storage
     *
     * @param file     The file to upload
     * @param fileName The storage file name
     * @return The presigned URL of the stored file, or {@code null} if the file is {@code null}, or empty
     * @throws IOException         If the storing operations fails
     * @throws BadRequestException If the provided file content type is not allowed
     */
    private String storeFileIfExists(MultipartFile file, String fileName) throws IOException {
        if (file == null || file.isEmpty()) return null;

        if (!isAllowedContentType(file.getContentType())) {
            throw new BadRequestException("Invalid file type", "INVALID_FILE_TYPE");
        }

        fileStorageService.store(fileName, file.getBytes(), file.getContentType());
        return fileStorageService.createPresignedUrl(fileName);
    }
}
