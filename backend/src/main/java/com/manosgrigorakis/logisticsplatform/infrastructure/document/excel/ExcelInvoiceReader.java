package com.manosgrigorakis.logisticsplatform.infrastructure.document.excel;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ExcelInvoiceImportDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ExcelInvoiceImportResultDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ExcelInvoiceReader {

    private static final String INVOICE_TYPE = "Τιμολόγιο Υπηρεσιών Μεταφοράς (Πρότυπο)";
    private static final String TIN = "Α.Φ.Μ. :";

    private static final Logger log = LoggerFactory.getLogger(ExcelInvoiceReader.class);

    public ExcelInvoiceImportResultDTO readExcel(MultipartFile importedFile) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(importedFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            String customerTin = "";
            List<ExcelInvoiceImportDTO> data = new ArrayList<>();

            for(Row row : sheet) {
                Cell customerTinHeaderCell = row.getCell(0);
                Cell customerTinCell = row.getCell(2);
                Cell invoiceIssueDateCell = row.getCell(0);
                Cell invoiceTypeCell = row.getCell(4);
                Cell invoiceNumberCell = row.getCell(9);
                Cell invoiceChargeCell = row.getCell(10);


                // Find customer TIN
                if (customerTinHeaderCell != null && customerTinHeaderCell.getCellType() == CellType.STRING) {
                  if (TIN.equals(customerTinHeaderCell.getStringCellValue())) {
                      if (customerTinCell.getCellType() == CellType.NUMERIC) {
                          customerTin = String.valueOf((long) customerTinCell.getNumericCellValue());
                      } else {
                          customerTin = customerTinCell.getStringCellValue();
                      }
                  }
                }

                if (invoiceTypeCell != null && invoiceTypeCell.getCellType() == CellType.STRING) {
                    if (INVOICE_TYPE.equals(invoiceTypeCell.getStringCellValue())) {
                        data.add(new ExcelInvoiceImportDTO(
                                        invoiceNumberCell.getRichStringCellValue().getString(),
                                        new BigDecimal(invoiceChargeCell.getNumericCellValue()),
                                        invoiceIssueDateCell.getLocalDateTimeCellValue().toLocalDate()
                                )
                        );

                    }
                }
            }

            return new ExcelInvoiceImportResultDTO(customerTin, data);
        } catch (IOException exc) {
            log.warn("Failed to process excel file", exc);
            throw new IOException("Failed to process excel file", exc);
        }
    }
}
