package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteRestController {
    private final QuoteService quoteService;

    public QuoteRestController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping()
    public Page<QuoteListResponseDTO> getAllQuotes(
            @ModelAttribute @Valid QuoteFilterRequest quoteFilter,
            @ModelAttribute @Valid PageFilterRequest page,
            @ModelAttribute SortFilterRequest sort
            )
    {
        return quoteService.getAllQuotes(quoteFilter, page, sort);
    }

    @GetMapping("/{id}")
    public QuoteResponseDTO getQuoteById(@PathVariable Long id) {
        return quoteService.getQuoteById(id);
    }

    @PostMapping()
    public ResponseEntity<QuoteCreatedResponseDTO> createQuote(@RequestBody @Valid QuoteRequestDTO dto) {
        QuoteCreatedResponseDTO response = quoteService.createQuote(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public QuoteResponseDTO updateQuoteById(@PathVariable Long id, @RequestBody @Valid QuoteUpdateRequestDTO dto) {
        return quoteService.updateQuote(id, dto);
    }
}
