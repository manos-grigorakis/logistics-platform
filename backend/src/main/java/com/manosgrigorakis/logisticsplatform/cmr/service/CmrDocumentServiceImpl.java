package com.manosgrigorakis.logisticsplatform.cmr.service;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UpdateCmrDocumentStatusRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UploadCmrDocumentRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.mapper.CmrDocumentMapper;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.cmr.repository.CmrDocumentRepository;
import com.manosgrigorakis.logisticsplatform.cmr.specs.CmrDocumentSpecs;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import com.manosgrigorakis.logisticsplatform.common.utils.SpecsUtils;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.PdfCmrDocumentService;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CmrDocumentServiceImpl implements CmrDocumentService {
    private final CmrDocumentRepository cmrDocumentRepository;

    private static final Logger log = LoggerFactory.getLogger(CmrDocumentServiceImpl.class);
    private final DocumentNumberGenerator documentNumberGenerator;
    private final FileStorageService fileStorageService;
    private final PdfCmrDocumentService pdfCmrDocumentService;

    @Value("${app.minio.bucketPathCmr}")
    private String bucketPathCmr;

    public CmrDocumentServiceImpl(CmrDocumentRepository cmrDocumentRepository, DocumentNumberGenerator documentNumberGenerator, FileStorageService fileStorageService, PdfCmrDocumentService pdfCmrDocumentService) {
        this.cmrDocumentRepository = cmrDocumentRepository;
        this.documentNumberGenerator = documentNumberGenerator;
        this.fileStorageService = fileStorageService;
        this.pdfCmrDocumentService = pdfCmrDocumentService;
    }

    @Override
    public Page<CmrDocumentResponseDTO> getAllCmrDocuments(
            CmrDocumentFilterRequest filterRequest,
            PageFilterRequest page,
            SortFilterRequest sort
    ) {
        Specification<CmrDocument> spec = Specification.allOf();
        spec = SpecsUtils.andIf(spec, filterRequest.getNumber(), CmrDocumentSpecs::likeNumber);
        spec = SpecsUtils.andIf(spec, filterRequest.getStatus(), CmrDocumentSpecs::equalCmrDocumentStatus);

        Pageable pageable = PageRequest.of(page.getPage(), page.getSize(), sort.createSort());
        Page<CmrDocument> cmrDocumentPage = this.cmrDocumentRepository.findAll(spec, pageable);

        return cmrDocumentPage.map(CmrDocumentMapper::toResponse);
    }

    @Override
    public CmrDocumentResponseDTO getCmrDocumentById(Long id) {
        CmrDocument cmrDocument = this.cmrDocumentRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("CMR Document not found with id {}", id);
                            return new ResourceNotFoundException("CMR Document not found with id: " + id);
                        }
                );

        String presignedUrl = fileStorageService.createPresignedUrl(this.bucketPathCmr + cmrDocument.getNumber());
        CmrDocumentResponseDTO response = CmrDocumentMapper.toResponse(cmrDocument);
        response.setFileUrl(presignedUrl);
        return response;
    }

    @Override
    public void createCmrDocument(Quote quote, Shipment shipment) {
        int currentYear = LocalDate.now().getYear();
        String lastNumber = this.cmrDocumentRepository.findLastCmrDocumentNumberByYear(currentYear)
                .orElse("CMR-" + currentYear + "-0000");

        String newNumber = documentNumberGenerator.generateNextSequentialNumber("CMR", lastNumber);

        CmrDocument cmrDocument = CmrDocument.builder()
                .number(newNumber)
                .shipment(shipment)
                .status(CmrStatus.GENERATED)
                .build();

        String presignedUrl = fileStorageService.createPresignedUrl(this.bucketPathCmr + cmrDocument.getNumber());
        cmrDocument.setFileUrl(presignedUrl);

        this.cmrDocumentRepository.save(cmrDocument);
        log.info("CMR Document saved with number {}", cmrDocument.getNumber());

        // Generate PDF
        byte[] cmrDocumentPdf = pdfCmrDocumentService.generateCmrDocumentPdf(quote, shipment, cmrDocument);

        // Upload the generated PDF to S3
        fileStorageService.store(
                this.bucketPathCmr + cmrDocument.getNumber(),
                cmrDocumentPdf,
                "application/pdf"
        );
    }

    @Override
    public void updateCmrDocumentStatus(Long id, UpdateCmrDocumentStatusRequestDTO dto) {
        CmrDocument cmrDocument = this.cmrDocumentRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("CMR Document not found with id {}", id);
                            return new ResourceNotFoundException("CMR Document not found with id: " + id);
                        }
                );

        try {
            cmrDocument.changeStatusTo(dto.getStatus());
        } catch (IllegalStateException e) {
            throw new ConflictException(
                    e.getMessage(),
                    Map.of("currentStatus", cmrDocument.getStatus(), "desiredStatus", dto.getStatus())
            );
        }

        cmrDocument.setStatus(dto.getStatus());
        this.cmrDocumentRepository.save(cmrDocument);
    }

    @Override
    public void uploadSignedCmrDocument(Long id, MultipartFile file, UploadCmrDocumentRequestDTO dto) {
        CmrDocument cmrDocument = this.cmrDocumentRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("CMR Document not found with id {}", id);
                            return new ResourceNotFoundException("CMR Document not found with id: " + id);
                        }
                );

        if (!cmrDocument.canChangeStatusTo(CmrStatus.SIGNED)) {
            throw new ConflictException(
                    "CMR document with number " + cmrDocument.getNumber() + " is already signed",
                    Map.of("currentStatus", cmrDocument.getStatus())
            );
        }

        byte[] fileInBytes = convertFileToBytesArray(file);

        // Upload signed CMR file to S3
        fileStorageService.store(
                this.bucketPathCmr + cmrDocument.getNumber() + "-SIGNED",
                fileInBytes,
                "application/pdf"
        );

        cmrDocument.setStatus(CmrStatus.SIGNED);
        cmrDocument.setSignedBy(dto.getSignedBy());
        cmrDocument.setSignedAt(LocalDateTime.now());
        this.cmrDocumentRepository.save(cmrDocument);
        log.info("Signed CMR PDF successfully updated and stored");
    }

    /**
     * Converts a file to a bytes array
     * @param file The file to be converted
     * @return The converted file in a bytes array
     */
    private byte[] convertFileToBytesArray(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.warn("Failed to read signed CMR file bytes");
            throw new RuntimeException(e);
        }
    }
}
