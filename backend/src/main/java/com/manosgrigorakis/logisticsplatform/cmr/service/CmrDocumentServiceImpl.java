package com.manosgrigorakis.logisticsplatform.cmr.service;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.mapper.CmrDocumentMapper;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.cmr.repository.CmrDocumentRepository;
import com.manosgrigorakis.logisticsplatform.cmr.specs.CmrDocumentSpecs;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
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

import java.time.LocalDate;

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

        CmrDocumentResponseDTO response = CmrDocumentMapper.toResponse(cmrDocument);
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

        String presignedUrl = fileStorageService.createPresignedUrl(cmrDocument.getNumber());
        cmrDocument.setFileUrl(presignedUrl);

        this.cmrDocumentRepository.save(cmrDocument);
        log.info("CMR Document saved with number {}", cmrDocument.getNumber());

        // TODO: Generate PDF and upload it to MinIO
        byte[] cmrDocumentPdf = pdfCmrDocumentService.generateCmrDocumentPdf(quote, shipment, cmrDocument);

        // Refactor to store it in separate bucket in MinIO
        fileStorageService.store(
                this.bucketPathCmr + cmrDocument.getNumber(),
                cmrDocumentPdf,
                "application/pdf"
        );
    }

    @Override
    public CmrDocumentResponseDTO updateCmrDocumentStatus(Long id) {
        return null;
    }

    @Override
    public CmrDocumentResponseDTO uploadSignedCmrDocument(Long id) {
        return null;
    }
}
