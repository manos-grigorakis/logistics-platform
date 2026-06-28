package com.manosgrigorakis.logisticsplatform.quotes.mapper;

import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemResponseDTO;
import com.manosgrigorakis.logisticsplatform.quotes.model.QuoteItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteItemMapper {
    QuoteItem toEntity(QuoteItemRequestDTO dto);

    QuoteItemResponseDTO toResponse(QuoteItem quoteItem);
}
