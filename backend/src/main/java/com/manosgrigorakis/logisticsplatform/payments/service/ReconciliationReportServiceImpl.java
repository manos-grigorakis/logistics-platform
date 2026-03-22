package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.payments.dto.CreateReconciliationReport;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportCreateResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.ReconciliationReport;
import com.manosgrigorakis.logisticsplatform.payments.repository.ReconciliationReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class ReconciliationReportServiceImpl implements ReconciliationReportService {
    private final ReconciliationReportRepository reconciliationReportRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.minio.bucketPathReconciliationReport}")
    private String bucketPathReport;

    @Override
    public ReconciliationReportResponseDTO getReconciliationReport(Long id) {
        ReconciliationReport report = reconciliationReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Report not found with id: {}", id);
                    return new ResourceNotFoundException("Report not found with id: " + id);
                });

        String fileName = report.getName() + ".xlsx";
        String presignedUrl = fileStorageService.createPresignedUrl(this.bucketPathReport + fileName);

        return new ReconciliationReportResponseDTO(
                report.getId(),
                report.getName(),
                presignedUrl,
                report.getFromDate(),
                report.getToDate(),
                report.getMatchedInvoices(),
                report.getUnmatchedInvoices(),
                report.getCustomer().getId(),
                report.getCreatedAt()
        );
    }

    @Override
    public ReconciliationReportCreateResponseDTO createReconciliationReport(CreateReconciliationReport dto) {
        Customer customer = dto.customer();

        String daysRange = dto.fromDate() + "-" + dto.toDate();
        String normalizeName = normalize(customer.getCompanyName()) + "-" + daysRange;
        String fileName = normalizeName + ".xlsx";

        byte[] file = dto.file().toByteArray();

        fileStorageService.store(this.bucketPathReport + fileName, file,
                                 "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String presignedUrl = fileStorageService.createPresignedUrl(this.bucketPathReport + fileName);

        ReconciliationReport report = ReconciliationReport.builder()
                .name(normalizeName)
                .fileUrl(presignedUrl)
                .fromDate(dto.fromDate())
                .toDate(dto.toDate())
                .matchedInvoices(dto.matchedInvoices())
                .unmatchedInvoices(dto.unmatchedInvoices())
                .customer(customer)
                .build();

        reconciliationReportRepository.save(report);
        log.info("Created reconciliation report {} for customer {}", fileName, customer.getCompanyName());
        return new ReconciliationReportCreateResponseDTO(report.getId(), report.getName(), report.getFileUrl(),
                                                         report.getFromDate(), report.getToDate(),
                                                         report.getCustomer().getId(), report.getCreatedAt());
    }

    /**
     * Normalize a string value by trimming whitespaces, converting all characters to lowercase and replacing white
     * spaces in between with {@code -}
     * <p>e.g. {@code ACME Logistics} -> {@code acme-logistics}</p>
     *
     * @param value The value to be normalized
     * @return The normalized {@link String}
     */
    private static String normalize(String value) {
        return value.trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");
    }
}
