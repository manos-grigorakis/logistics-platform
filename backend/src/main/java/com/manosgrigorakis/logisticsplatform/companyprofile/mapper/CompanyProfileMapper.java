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
                .representativeTitle(request.representativeTitle())
                .representative(request.representative())
                .slogan(request.slogan())
                .email(request.email())
                .phones(request.phones())
                .websiteUrl(request.websiteUrl())
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
    public static CompanyProfile toUpdate(CompanyProfileUpdateRequest request, CompanyProfile entity) {
        entity.setName(request.name());
        entity.setVatPercentage(request.vatPercentage());
        entity.setRepresentativeTitle(request.representativeTitle());
        entity.setRepresentative(request.representative());
        entity.setSlogan(request.slogan());
        entity.setEmail(request.email());
        entity.setPhones(request.phones());
        entity.setWebsiteUrl(request.websiteUrl());
        entity.setStreet(request.street());
        entity.setStreetNumber(request.streetNumber());
        entity.setPostalCode(request.postalCode());
        entity.setRegion(request.region());
        entity.setCountry(request.country());
        entity.setBrandPrimaryColor(
                request.brandPrimaryColor() != null ? request.brandPrimaryColor() : entity.getBrandPrimaryColor());
        entity.setBrandSecondaryColor(
                request.brandSecondaryColor() != null ? request.brandSecondaryColor() : entity.getBrandSecondaryColor());

        return entity;
    }

    // Entity -> Response
    public static CompanyProfileResponse toResponse(CompanyProfile entity, String logoUrl) {
        CompanyProfileAddressSummary addressSummary = new CompanyProfileAddressSummary(entity.getStreet(),
                                                                                       entity.getStreetNumber(),
                                                                                       entity.getPostalCode(),
                                                                                       entity.getRegion(),
                                                                                       entity.getCountry());
        CompanyProfileBrandingSummary brandingSummary = new CompanyProfileBrandingSummary(entity.getBrandPrimaryColor(),
                                                                                          entity.getBrandSecondaryColor());

        return new CompanyProfileResponse(entity.getId(), entity.getName(), entity.getTin(), entity.getSlogan(),
                                          logoUrl,
                                          entity.getVatPercentage(), entity.getRepresentativeTitle(),
                                          entity.getRepresentative(), entity.getEmail(), entity.getPhones(), entity.getWebsiteUrl(),
                                          addressSummary,
                                          brandingSummary, entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
