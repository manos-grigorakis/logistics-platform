package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteResponseDTO;
import org.springframework.data.domain.Page;

public interface QuoteService {
    Page<QuoteResponseDTO> getAllQuotes();

    QuoteResponseDTO getQuoteById(Long id);

    QuoteResponseDTO createQuote(QuoteRequestDTO dto);

    QuoteResponseDTO updateQuote(Long id, QuoteRequestDTO dto);
}
