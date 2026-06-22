package com.manosgrigorakis.logisticsplatform.companyprofile.dto;

import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileAddressSummary;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileBrandingSummary;

import java.time.LocalDateTime;

public record CompanyProfileResponse(
        Long id,
        String name,
        String tin,
        String logoUrl,
        Integer vatPercentage,
        String representative,
        CompanyProfileAddressSummary address,
        CompanyProfileBrandingSummary branding,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
