package com.manosgrigorakis.logisticsplatform.cmr.repository;

import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CmrDocumentRepository extends JpaRepository<CmrDocument, Long>, JpaSpecificationExecutor<CmrDocument> {
    @Query(
            "SELECT cmr.number FROM CmrDocument AS cmr " +
                    "WHERE Year(cmr.createdAt) = :year " +
                    "ORDER BY cmr.id DESC LIMIT 1"
    )
    Optional<String> findLastCmrDocumentNumberByYear(@Param("year") int year);

    Optional<CmrDocument> findCmrDocumentByShipmentId(Long shipmentId);
}
