package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteCreatedResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.service.QuoteService;
import jakarta.validation.Valid;
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

    @PostMapping()
    public ResponseEntity<QuoteCreatedResponseDTO> createQuote(@RequestBody @Valid QuoteRequestDTO dto) {
        QuoteCreatedResponseDTO response = quoteService.createQuote(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pdf")
    public void testMyPdF() {}

}
