package com.manosgrigorakis.logisticsplatform.scheduling;

import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class QuotesScheduling {
    private final QuoteRepository quoteRepository;

    private final Logger log = LoggerFactory.getLogger(QuotesScheduling.class);

    public QuotesScheduling(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Athens") // Every day at 00:00
    @Transactional
    public void setExpiredQuotes() {
        List<Quote> expiredQuotes = quoteRepository.findExpiredQuotes(List.of(QuoteStatus.DRAFT, QuoteStatus.SENT));

        for(Quote quote : expiredQuotes) {
            quote.setQuoteStatus(QuoteStatus.EXPIRED);
        }

        log.info("Scheduled job set {} as expired quotes", expiredQuotes.size());
    }
}
