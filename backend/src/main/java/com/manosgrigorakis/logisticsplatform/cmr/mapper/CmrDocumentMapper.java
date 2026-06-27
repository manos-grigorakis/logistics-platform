package com.manosgrigorakis.logisticsplatform.cmr.mapper;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentListResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CmrDocumentMapper {
    @Mapping(target = "carriedSigned", source = "carrierSigned")
    @Mapping(target = "shipmentId", source = "shipment.id")
    CmrDocumentResponseDTO toResponse(CmrDocument cmrDocument);

    @Mapping(target = "carriedSigned", source = "carrierSigned")
    @Mapping(target = "shipmentId", source = "shipment.id")
    CmrDocumentListResponseDTO toResponseList(CmrDocument cmrDocument);
}
