package com.manosgrigorakis.logisticsplatform.mapper;

import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteCreatedResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.quote.QuoteResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.model.Customer;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.model.User;

import java.util.List;

public class QuoteMapper {
    // DTO => Entity
    public static Quote toEntity(QuoteRequestDTO dto, User user, Customer customer) {
        Quote quote = Quote.builder()
                .validityDays(dto.getValidityDays())
                .origin(dto.getOrigin())
                .destination(dto.getDestination())
                .notes(dto.getNotes())
                .specialTerms(dto.getSpecialTerms())
                .quoteStatus(dto.getQuoteStatus())
                .user(user)
                .customer(customer)
                .build();

        for (QuoteItemRequestDTO itemDto : dto.getQuoteItems()) {
            QuoteItem item = QuoteItemMapper.toEntity(itemDto);
            quote.addQuoteItem(item);
        }

        return quote;
    }

    // Entity => Response
    public static QuoteResponseDTO toResponse(Quote quote) {
        QuoteResponseDTO dto = new QuoteResponseDTO();
        dto.setId(quote.getId());
        dto.setNumber(quote.getNumber());
        dto.setIssueDate(quote.getIssueDate());
        dto.setValidityDays(quote.getValidityDays());
        dto.setExpirationDate(quote.getExpirationDate());
        dto.setOrigin(quote.getOrigin());
        dto.setDestination(quote.getDestination());
        dto.setTaxRatePercentage(quote.getTaxRatePercentage());
        dto.setNetPrice(quote.getNetPrice());
        dto.setVatAmount(quote.getVatAmount());
        dto.setGrossPrice(quote.getGrossPrice());
        dto.setNotes(quote.getNotes());
        dto.setSpecialTerms(quote.getSpecialTerms());
        dto.setQuoteStatus(quote.getQuoteStatus());
        dto.setCreatedAt(quote.getCreatedAt());
        dto.setUpdatedAt(quote.getUpdatedAt());

        if (quote.getUser() != null) {
            dto.setUserId(quote.getUser().getId());
        }

        if (quote.getCustomer() != null) {
            dto.setCustomerId(quote.getCustomer().getId());
        }

        List<QuoteItem> quoteItems = quote.getQuoteItems();


        dto.setQuoteItems(quoteItems
                .stream()
                .map(QuoteItemMapper::toResponse)
                .toList());

        return dto;
    }

    // Created Entity -> Response
    public static QuoteCreatedResponseDTO toCreatedResponse(Quote quote) {
        QuoteCreatedResponseDTO dto = new QuoteCreatedResponseDTO();
        dto.setId(quote.getId());
        dto.setNumber(quote.getNumber());
        dto.setIssueDate(quote.getIssueDate());
        dto.setExpirationDate(quote.getExpirationDate());
        dto.setGrossPrice(quote.getGrossPrice());
        dto.setQuoteStatus(quote.getQuoteStatus());

        // Set Customer and full name
        if (quote.getCustomer() != null) {
            dto.setCustomerId(quote.getCustomer().getId());

            if (quote.getCustomer().getFullName() != null) {
                dto.setCustomerFullName(quote.getCustomer().getFullName());
            }
        }

        return dto;
    }
}
