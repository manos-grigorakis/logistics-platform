package com.manosgrigorakis.logisticsplatform.infrastructure.document.excel;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class ExcelBankTransactionReaderNgb {
    private final String DATE_COLUMN = "Ημερομηνία";
    private final String TRANSACTION_AMOUNT_COLUMN = "Ποσό συναλλαγής";
    private final String DESCRIPTION_COLUMN = "Περιγραφή";
    private final String COUNTER_PARTY_COLUMN = "Ονοματεπώνυμο αντισυμβαλλόμενου";

    private static final Logger log = LoggerFactory.getLogger(ExcelBankTransactionReaderNgb.class);

    public List<BankStatementImportResultDTO> readExcel(MultipartFile file) throws IOException {
        log.info("Processing bank transaction Excel file {}", file.getOriginalFilename());

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<BankStatementImportResultDTO> data = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            Cell dateHeaderCell = headerRow.getCell(1);
            Cell transactionHeaderCell = headerRow.getCell(7);
            Cell descriptionHeaderCell = headerRow.getCell(12);
            Cell counterPartyHeaderCell = headerRow.getCell(15);

            if (!validateHeader(dateHeaderCell, DATE_COLUMN) ||
                    !validateHeader(transactionHeaderCell, TRANSACTION_AMOUNT_COLUMN) ||
                    !validateHeader(descriptionHeaderCell, DESCRIPTION_COLUMN) ||
                    !validateHeader(counterPartyHeaderCell, COUNTER_PARTY_COLUMN))
            {
                log.warn("Invalid bank statement Excel format for file {}", file.getOriginalFilename());
                return data;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell dateCell = row.getCell(1);
                Cell transactionCell = row.getCell(7);
                Cell descriptionCell = row.getCell(12);
                Cell counterPartyCell = row.getCell(15);

                data.add(new BankStatementImportResultDTO(
                        convertStringToLocalDate(dateCell.getStringCellValue().trim()),
                        BigDecimal.valueOf(transactionCell.getNumericCellValue()).
                                setScale(2, RoundingMode.HALF_UP),
                        descriptionCell.getStringCellValue().trim(),
                        counterPartyCell.getStringCellValue().trim())
                );
            }
            return data;
        }
    }

    /**
     * Validates if a header in the Excel file matches the validator
     * @param cell The {@link Cell} to be checked
     * @param expected The validation of the {@link Cell}
     * @return {@code true} if the header matches, otherwise {@code false} if header is {@code null} or doesn't match
     */
    private boolean validateHeader(Cell cell, String expected) {
        if (cell == null) return false;
        return expected.equals(cell.getStringCellValue().trim());
    }

    /**
     * Converts a date in {@link String} format into {@link LocalDate} using {@code dd/MM/yyyy} format
     * @param date The date in {@link String} format to be converted
     * @return The converted {@link LocalDate} date
     */
    private static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
