package com.manosgrigorakis.logisticsplatform.companyprofile.service;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.common.exception.ConflictException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.exception.StorageServiceException;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileCreateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileResponse;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileUpdateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.mapper.CompanyProfileMapper;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import com.manosgrigorakis.logisticsplatform.companyprofile.repository.CompanyProfileRepository;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {
    private final CompanyProfileRepository companyProfileRepository;
    private final FileStorageService fileStorageService;
    private final CompanyProfileMapper companyProfileMapper;

    @Value("${app.minio.bucketPathCompanyProfile}")
    private String companyProfileBucketPath;

    private final List<String> allowedContentTypes = List.of("image/jpeg", "image/png");
    private final int maxLogoFileSize = 2 * 1024 * 1024; // 2ΜΒ
    private final String fileKey = "company-logo";

    /**
     * Returns the first and only company profile that exists in the database
     *
     * @return The first and only company profile
     * @throws ResourceNotFoundException If the company profile not found
     */
    @Cacheable(value = "company-profile", key = "'entity'")
    @Override
    public CompanyProfile getCompanyProfileEntity() throws ResourceNotFoundException {
        return companyProfileRepository.findFirstByOrderByIdAsc().orElseThrow(() -> {
            log.warn("Company profile not found");
            return new ResourceNotFoundException("Company profile not found");
        });
    }

    @Cacheable(value = "company-profile", key = "'full-response'")
    @Override
    public CompanyProfileResponse getCompanyProfile() {
        CompanyProfile company = getCompanyProfileEntity();

        return companyProfileMapper.toResponse(company, createPresignedUrl(company));
    }

    @Override
    public CompanyProfileResponse createCompanyProfile(CompanyProfileCreateRequest request) {
        if (companyProfileRepository.findFirstByOrderByIdAsc().isPresent()) {
            log.warn("Attempt to create company profile when company profile already exists");
            throw new ConflictException("Company profile already exists", "COMPANY_PROFILE_ALREADY_EXISTS");
        }

        String logoKey = companyProfileBucketPath + fileKey;
        storeLogoFileIfExists(request.logoFile(), logoKey);
        CompanyProfile companyProfile = companyProfileMapper.toEntity(request);

        if(request.logoFile() != null && !request.logoFile().isEmpty()) companyProfile.setLogoUrl(logoKey);

        try {
            CompanyProfile saved = companyProfileRepository.save(companyProfile);
            log.info("Company profile created successfully");
            return companyProfileMapper.toResponse(saved, createPresignedUrl(companyProfile));
        } catch (Exception e) {
            log.error("Error while creating company profile", e);
            fileStorageService.deleteObject(logoKey);
            log.info("Company profile logo deleted successfully");
            throw e;
        }
    }

    @CacheEvict(value = "company-profile", allEntries = true)
    @Override
    public CompanyProfileResponse updateCompanyProfile(CompanyProfileUpdateRequest request) {
        CompanyProfile company = getCompanyProfileEntity();

        String logoKey = companyProfileBucketPath + fileKey;
        storeLogoFileIfExists(request.logoFile(), logoKey);
        companyProfileMapper.toUpdate(company, request);

        if(request.logoFile() != null && !request.logoFile().isEmpty()) company.setLogoUrl(logoKey);

        try {
            CompanyProfile saved = companyProfileRepository.save(company);
            log.info("Company profile updated successfully");
            return companyProfileMapper.toResponse(saved, createPresignedUrl(company));
        } catch (Exception e) {
            log.error("Error while updating company profile", e);
            fileStorageService.deleteObject(logoKey);
            log.info("Company profile logo deleted successfully");
            throw e;
        }
    }

    /**
     * Validates and stores the provided Logo file in the storage
     * <p>Validation - {@link #validateLogoFile(MultipartFile)}</p>
     *
     * @param file    The Logo file to store
     * @param fileKey The full file key along with the prefix of the bucket
     * @throws StorageServiceException If the storing of the logo fails {@code STORAGE_ERROR}
     */
    private void storeLogoFileIfExists(MultipartFile file, String fileKey) throws StorageServiceException {
        if (file != null && !file.isEmpty()) {
            validateLogoFile(file);

            try {
                fileStorageService.store(fileKey, file.getBytes(), file.getContentType());
                log.info("Company profile logo stored successfully");
            } catch (IOException e) {
                log.error("Error while saving company logo file", e);
                throw new StorageServiceException("Failed to store company logo", "STORAGE_ERROR");
            }
        }
    }

    /**
     * Validates logo file extension type and file size
     *
     * @param file The Logo file to validate
     * @throws BadRequestException If the content type is invalid {@code INVALID_FILE_TYPE} or the file size exceeds the
     *                             maximum {@code FILE_TOO_LARGE}
     */
    private void validateLogoFile(MultipartFile file) throws BadRequestException {
        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new BadRequestException("Invalid logo file contentType", "INVALID_FILE_TYPE",
                                          Map.of(
                                                  "allowedContentTypes", allowedContentTypes.toString()));
        }

        if (file.getSize() > maxLogoFileSize) {
            double maxSizeMb = maxLogoFileSize / (1024.0 * 1024.0);
            throw new BadRequestException("Invalid logo file size", "FILE_TOO_LARGE",
                                          Map.of("maxLogoSize", maxSizeMb + "ΜΒ"));
        }
    }

    /**
     * Creates a presigned URL for the Logo of the company profile
     *
     * @param companyProfile The company profile entity
     * @return The Logo URL if exists, otherwise {@code null}
     */
    private String createPresignedUrl(CompanyProfile companyProfile) {
        return companyProfile.getLogoUrl() != null ? fileStorageService.createPresignedUrl(
                companyProfile.getLogoUrl()) : null;
    }
}
