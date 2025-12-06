package com.manosgrigorakis.logisticsplatform.auth.service;

import com.manosgrigorakis.logisticsplatform.auth.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.auth.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.common.utils.GenerateSecureToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserTokensServiceImpl implements UserTokensService {
    private final UserTokensRepository userTokensRepository;
    private final Logger log = LoggerFactory.getLogger(UserTokensServiceImpl.class);

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
        log.info("Token {} for user {} created", tokenType, user.getEmail());

        return userTokens;
    }
}
