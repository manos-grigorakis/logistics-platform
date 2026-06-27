package com.manosgrigorakis.logisticsplatform.companyprofile.mapper;

import com.manosgrigorakis.logisticsplatform.companyprofile.dto.*;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyProfileMapper {
    CompanyProfile toEntity(CompanyProfileCreateRequest request);

    @Mapping(target = "brandPrimaryColor", expression =
            "java(request.brandPrimaryColor() != null ? request.brandPrimaryColor() : entity.getBrandPrimaryColor())")
    @Mapping(target = "brandSecondaryColor", expression =
            "java(request.brandSecondaryColor() != null ? request.brandSecondaryColor() : entity.getBrandSecondaryColor())")
    void toUpdate(@MappingTarget CompanyProfile entity, CompanyProfileUpdateRequest request);

    @Mapping(target = "logoUrl", source = "logoUrl")
    @Mapping(target = "address.street", source = "entity.street")
    @Mapping(target = "address.streetNumber", source = "entity.streetNumber")
    @Mapping(target = "address.postalCode", source = "entity.postalCode")
    @Mapping(target = "address.region", source = "entity.region")
    @Mapping(target = "address.country", source = "entity.country")
    @Mapping(target = "branding.primaryColor", source = "entity.brandPrimaryColor")
    @Mapping(target = "branding.secondaryColor", source = "entity.brandSecondaryColor")
    CompanyProfileResponse toResponse(CompanyProfile entity, String logoUrl);
}
