package com.manosgrigorakis.logisticsplatform.repository;

import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokensRepository extends JpaRepository<UserTokens, Long> {
    Optional<UserTokens> findByToken(String token);
}
