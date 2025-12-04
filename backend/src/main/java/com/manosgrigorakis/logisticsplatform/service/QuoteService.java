package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import org.springframework.data.domain.Page;

public interface QuoteService {
    Page<QuoteListResponseDTO> getAllQuotes(QuoteFilterRequest quoteFilter, PageFilterRequest page, SortFilterRequest sort);

    QuoteResponseDTO getQuoteById(Long id);

    QuoteCreatedResponseDTO createQuote(QuoteRequestDTO dto);

    QuoteResponseDTO updateQuote(Long id, QuoteUpdateRequestDTO dto);
}
