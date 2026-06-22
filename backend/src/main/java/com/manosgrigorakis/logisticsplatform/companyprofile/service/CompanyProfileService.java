package com.manosgrigorakis.logisticsplatform.companyprofile.service;

import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileCreateRequest;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileResponse;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.CompanyProfileUpdateRequest;

public interface CompanyProfileService {
    CompanyProfileResponse getCompanyProfile();

    CompanyProfileResponse createCompanyProfile(CompanyProfileCreateRequest request);

    CompanyProfileResponse updateCompanyProfile(CompanyProfileUpdateRequest request);
}
