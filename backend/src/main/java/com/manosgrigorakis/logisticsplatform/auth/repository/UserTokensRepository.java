package com.manosgrigorakis.logisticsplatform.auth.repository;

import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserTokensRepository extends JpaRepository<UserTokens, Long> {
    Optional<UserTokens> findByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime current);

    Optional<UserTokens> findByUserId(Long userId);
}
