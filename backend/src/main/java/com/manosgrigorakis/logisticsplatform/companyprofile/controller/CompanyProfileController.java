package com.manosgrigorakis.logisticsplatform.companyprofile.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileCreateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileResponse;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileUpdateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.service.CompanyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company Profile", description = "GET, CREATE, UPDATE operations for company profile")
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/company-profile")
@RestController
public class CompanyProfileController {
    private final CompanyProfileService companyProfileService;

    @Operation(summary = "Get Company Profile", description = "Lists company profile details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found company profile"),
            @ApiResponse(responseCode = "404", description = "Company profile not found")
    })
    @GetMapping
    public ApiResponseWrapper<CompanyProfileResponse> getCompanyProfile() {
        return new ApiResponseWrapper<>(companyProfileService.getCompanyProfile());
    }

    @Operation(summary = "Creates a new Company Profile", description =
            "Creates a new company profile. Only one company can be created")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company profile created"),
            @ApiResponse(responseCode = "400", description = "Validation fields, file (MIME Type + file size)"),
            @ApiResponse(responseCode = "409", description = "Company profile already exists"),
            @ApiResponse(responseCode = "503", description = "Storage error or DB error"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<CompanyProfileResponse> createCompanyProfile(
            @ModelAttribute @Valid CompanyProfileCreateRequest request) {
        return new ApiResponseWrapper<>(companyProfileService.createCompanyProfile(request));
    }

    @Operation(summary = "Updates a Company Profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated company profile"),
            @ApiResponse(responseCode = "400", description = "Validation fields, file (MIME Type + file size)"),
            @ApiResponse(responseCode = "404", description = "Company profile not found"),
            @ApiResponse(responseCode = "503", description = "Storage error or DB error"),
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<CompanyProfileResponse> updateCompanyProfile(
            @ModelAttribute @Valid CompanyProfileUpdateRequest request) {
        return new ApiResponseWrapper<>(companyProfileService.updateCompanyProfile(request));
    }
}
