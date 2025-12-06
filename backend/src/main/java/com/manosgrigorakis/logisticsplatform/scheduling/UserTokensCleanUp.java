package com.manosgrigorakis.logisticsplatform.scheduling;

import com.manosgrigorakis.logisticsplatform.auth.repository.UserTokensRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UserTokensCleanUp {
    private final UserTokensRepository userTokensRepository;

    private final Logger log = LoggerFactory.getLogger(UserTokensCleanUp.class);

    public UserTokensCleanUp(UserTokensRepository userTokensRepository) {
        this.userTokensRepository = userTokensRepository;
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "Europe/Athens") // Every day at 4:00
    @Transactional
    public void clearExpiredTokens() {
        userTokensRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
        log.info("Scheduled job to clear expired user tokens");
    }
}
