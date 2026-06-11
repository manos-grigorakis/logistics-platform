package com.manosgrigorakis.logisticsplatform.cmr.dto;

public record DownloadAllCmrCopiesResponse(String cmrNumber, byte[] file) {
}
