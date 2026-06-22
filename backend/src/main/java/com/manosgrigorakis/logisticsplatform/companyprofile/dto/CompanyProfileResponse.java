package com.manosgrigorakis.logisticsplatform.companyprofile.dto;

import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileAddressSummary;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileBrandingSummary;

import java.time.LocalDateTime;
import java.util.List;

public record CompanyProfileResponse(
        Long id,
        String name,
        String tin,
        String slogan,
        String logoUrl,
        Integer vatPercentage,
        String representativeTitle,
        String representative,
        String email,
        List<String> phones,
        String websiteUrl,
        CompanyProfileAddressSummary address,
        CompanyProfileBrandingSummary branding,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
