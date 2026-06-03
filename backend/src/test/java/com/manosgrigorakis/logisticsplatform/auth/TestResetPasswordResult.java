package com.manosgrigorakis.logisticsplatform.auth;

import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.MessageResponseDTO;
import org.springframework.http.ResponseEntity;

public record TestResetPasswordResult(ResponseEntity<ApiResponseWrapper<MessageResponseDTO>> response, UserTokens userTokens) {
}
