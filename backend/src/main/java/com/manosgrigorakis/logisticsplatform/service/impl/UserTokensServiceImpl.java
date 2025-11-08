package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.service.UserTokensService;
import com.manosgrigorakis.logisticsplatform.utils.GenerateSecureToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserTokensServiceImpl implements UserTokensService {
    private final UserTokensRepository userTokensRepository;

    public UserTokensServiceImpl(UserTokensRepository userTokensRepository) {
        this.userTokensRepository = userTokensRepository;
    }

    @Override
    public UserTokens generateUserTokens(TokenType tokenType, Duration expiresIn, User user) {
        // Generate token
        String token = GenerateSecureToken.generateToken();

        // Set token expiration time e.g (24h, 10m, 20s)
        LocalDateTime expirationTime = LocalDateTime.now().plus(expiresIn);

        UserTokens userTokens = UserTokens.builder()
                .token(token)
                .type(tokenType)
                .expiresAt(expirationTime)
                .build();

        userTokens.setUser(user);
        userTokensRepository.save(userTokens);

        return userTokens;
    }
}
