package com.manosgrigorakis.logisticsplatform.payments.mapper;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;

public class BankTransactionMapper {
    // BankStatementImportResultDTO -> Entity
    public static BankTransaction toEntity(BankStatementImportResultDTO dto, String bankName) {
        return BankTransaction.builder()
                .bankName(bankName)
                .senderName(dto.counterPartyName())
                .amount(dto.transaction())
                .issueDate(dto.date())
                .description(dto.description())
                .build();
    }
}
