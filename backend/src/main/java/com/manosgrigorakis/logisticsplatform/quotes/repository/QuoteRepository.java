package com.manosgrigorakis.logisticsplatform.quotes.repository;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long>, JpaSpecificationExecutor<Quote> {
    @Query("SELECT q.number FROM Quote AS q " +
    "WHERE Year(q.createdAt) = :year " +
    "ORDER BY q.id DESC LIMIT 1")
    Optional<String> findLastQuoteNumberByYear(@Param("year") int year);

    @Query("SELECT q FROM Quote AS q " +
            "WHERE q.expirationDate <= CURRENT_DATE " +
        "AND q.quoteStatus IN :statuses"
    )
    List<Quote> findExpiredQuotes(@Param("statuses")List<QuoteStatus> statuses);

    List<Quote> findByCustomerId(Long id);
}
