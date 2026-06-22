package com.manosgrigorakis.logisticsplatform.companyprofile.repository;

import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findFirstByOrderByIdAsc();
}
