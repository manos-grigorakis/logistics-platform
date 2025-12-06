package com.manosgrigorakis.logisticsplatform.users.repository;

import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long countByRoleId(Long id);
}
