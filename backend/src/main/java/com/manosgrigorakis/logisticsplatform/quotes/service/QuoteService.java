package com.manosgrigorakis.logisticsplatform.quotes.service;

import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.*;
import org.springframework.data.domain.Page;

public interface QuoteService {
    Page<QuoteListResponseDTO> getAllQuotes(QuoteFilterRequest quoteFilter, PageFilterRequest page, SortFilterRequest sort);

    QuoteResponseDTO getQuoteById(Long id);

    QuoteCreatedResponseDTO createQuote(QuoteRequestDTO dto);

    QuoteResponseDTO updateQuote(Long id, QuoteUpdateRequestDTO dto);
}
