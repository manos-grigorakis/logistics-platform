package com.manosgrigorakis.logisticsplatform.companyprofile.mapper;

import com.manosgrigorakis.logisticsplatform.companyprofile.dto.*;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileAddressSummary;
import com.manosgrigorakis.logisticsplatform.companyprofile.dto.summary.CompanyProfileBrandingSummary;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;

public class CompanyProfileMapper {
    // Create -> Entity
    public static CompanyProfile toEntity(CompanyProfileCreateRequest request) {
        return CompanyProfile.builder()
                .name(request.name())
                .tin(request.tin())
                .vatPercentage(request.vatPercentage())
                .representative(request.representative())
                .street(request.street())
                .streetNumber(request.streetNumber())
                .postalCode(request.postalCode())
                .region(request.region())
                .country(request.country())
                .brandPrimaryColor(request.brandPrimaryColor())
                .brandSecondaryColor(request.brandSecondaryColor())
                .build();
    }

    // Update -> Entity
    public static CompanyProfile toUpdate(CompanyProfileUpdateRequest request, CompanyProfile entity, String logoUrl) {
        entity.setName(request.name());
        entity.setVatPercentage(request.vatPercentage());
        entity.setRepresentative(request.representative());
        entity.setStreet(request.street());
        entity.setStreetNumber(request.streetNumber());
        entity.setPostalCode(request.postalCode());
        entity.setRegion(request.region());
        entity.setCountry(request.country());
        entity.setBrandPrimaryColor(request.brandPrimaryColor());
        entity.setBrandSecondaryColor(request.brandSecondaryColor());
        entity.setLogoUrl(logoUrl);

        return entity;
    }

    // Entity -> Response
    public static CompanyProfileResponse toResponse(CompanyProfile entity) {
        CompanyProfileAddressSummary addressSummary = new CompanyProfileAddressSummary(entity.getStreet(),
                                                                                       entity.getStreetNumber(),
                                                                                       entity.getPostalCode(),
                                                                                       entity.getRegion(),
                                                                                       entity.getCountry());
        CompanyProfileBrandingSummary brandingSummary = new CompanyProfileBrandingSummary(entity.getBrandPrimaryColor(),
                                                                                          entity.getBrandSecondaryColor());

        return new CompanyProfileResponse(entity.getId(), entity.getName(), entity.getTin(), entity.getLogoUrl(),
                                          entity.getVatPercentage(), entity.getRepresentative(), addressSummary,
                                          brandingSummary, entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
