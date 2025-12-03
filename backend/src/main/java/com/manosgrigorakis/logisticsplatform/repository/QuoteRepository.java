package com.manosgrigorakis.logisticsplatform.repository;

import com.manosgrigorakis.logisticsplatform.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    @Query("SELECT q.number FROM Quote as q " +
    "WHERE Year(q.createdAt) = :year " +
    "ORDER BY q.id DESC LIMIT 1")
    Optional<String> findLastQuoteNumberByYear(@Param("year") int year);
}
