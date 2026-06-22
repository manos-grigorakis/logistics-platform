package com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary;

public record CompanyProfileAddressSummary(
        String street,
        String streetNumber,
        String postalCode,
        String region,
        String country
) {
}
