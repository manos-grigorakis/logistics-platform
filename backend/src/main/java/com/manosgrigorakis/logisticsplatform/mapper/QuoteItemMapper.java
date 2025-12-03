package com.manosgrigorakis.logisticsplatform.mapper;

import com.manosgrigorakis.logisticsplatform.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.quoteItem.QuoteItemResponseDTO;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;

public class QuoteItemMapper {
    // DTO => Entity
    public static QuoteItem toEntity(QuoteItemRequestDTO dto) {
        return QuoteItem.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
    }

    // Entity => Response
    public static QuoteItemResponseDTO toResponse(QuoteItem quoteItem) {
        QuoteItemResponseDTO dto = new QuoteItemResponseDTO();
        dto.setId(quoteItem.getId());
        dto.setName(quoteItem.getName());
        dto.setDescription(quoteItem.getDescription());
        dto.setPrice(quoteItem.getPrice());
        return dto;
    }
}
