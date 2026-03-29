package com.manosgrigorakis.logisticsplatform.auth;

import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.common.dto.MessageResponseDTO;
import org.springframework.http.ResponseEntity;

public record TestResetPasswordResult(ResponseEntity<MessageResponseDTO> response, UserTokens userTokens) {
}
