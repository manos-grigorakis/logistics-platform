package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import lombok.Getter;

@Getter
public class CmrDocumentFilterRequest {
    private String number;
    private CmrStatus status;
}
