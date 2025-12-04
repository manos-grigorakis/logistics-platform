package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteCreatedResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteUpdateRequestDTO;
import org.springframework.data.domain.Page;

public interface QuoteService {
    Page<QuoteResponseDTO> getAllQuotes();

    QuoteResponseDTO getQuoteById(Long id);

    QuoteCreatedResponseDTO createQuote(QuoteRequestDTO dto);

    QuoteResponseDTO updateQuote(Long id, QuoteUpdateRequestDTO dto);
}
