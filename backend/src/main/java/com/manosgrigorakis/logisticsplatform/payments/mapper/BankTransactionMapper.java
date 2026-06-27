package com.manosgrigorakis.logisticsplatform.payments.mapper;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankTransactionMapper {
    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "senderName", source = "dto.counterPartyName")
    @Mapping(target = "amount", source = "dto.transaction")
    @Mapping(target = "issueDate", source = "dto.date")
    BankTransaction toEntity(BankStatementImportResultDTO dto, String bankName);
}
