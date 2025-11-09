package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;

import java.time.Duration;

public interface UserTokensService {
    UserTokens generateUserTokens(TokenType tokenType, Duration expiresInMinutes, User user);
}
