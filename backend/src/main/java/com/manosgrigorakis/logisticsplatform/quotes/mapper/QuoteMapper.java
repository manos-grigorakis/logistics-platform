package com.manosgrigorakis.logisticsplatform.quotes.mapper;

import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;


@Mapper(componentModel = "spring", uses = QuoteItemMapper.class)
public interface QuoteMapper {
    @Mapping(target = "quoteItems", ignore = true)
    Quote toEntity(QuoteRequestDTO dto, User user, Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "quoteStatus", ignore = true)
    @Mapping(target = "quoteItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "netPrice", source = "netTotal")
    @Mapping(target = "grossPrice", source = "grossTotal")
    void toUpdate(@MappingTarget Quote quote, QuoteUpdateRequestDTO dto, BigDecimal netTotal, BigDecimal vatAmount,
                  BigDecimal grossTotal, Customer customer);

    @Mapping(target = "userId", source = "user.id")
    QuoteResponseDTO toResponse(Quote quote);

    @Mapping(target = "customerId", source = "quote.customer.id")
    @Mapping(target = "customerFullName", source = "quote.customer.fullName")
    QuoteCreatedResponseDTO toCreatedResponse(Quote quote);

    @Mapping(target = "status", source = "quote.quoteStatus")
    @Mapping(target = "companyName", source = "quote.customer.companyName")
    QuoteListResponseDTO toListResponse(Quote quote);

    QuoteItem toQuoteItem(QuoteItemRequestDTO dto);

    @AfterMapping
    default void linkQuoteItemsBackToQuote(@MappingTarget Quote quote, QuoteRequestDTO dto) {
        if(dto == null) return;

        dto.getQuoteItems().forEach(item ->
            quote.addQuoteItem(toQuoteItem(item)));
    }
}
