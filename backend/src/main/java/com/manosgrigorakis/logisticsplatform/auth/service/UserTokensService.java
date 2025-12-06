package com.manosgrigorakis.logisticsplatform.auth.service;

import com.manosgrigorakis.logisticsplatform.auth.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;

import java.time.Duration;

public interface UserTokensService {
    UserTokens generateUserTokens(TokenType tokenType, Duration expiresInMinutes, User user);
}
