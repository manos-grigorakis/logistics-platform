package com.manosgrigorakis.logisticsplatform.infrastructure.document.excel;

import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ReconciliationRow;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class ReconciliationReportExcelGenerator {
    private static final List<String> CUSTOMER_ROW_ONE = Arrays.asList("Α.Φ.Μ.", "Εταιρία", "Τύπος Πελάτη");
    private static final List<String> CUSTOMER_ROW_TWO = Arrays.asList("Τοποθεσία", "Τηλέφωνο", "Email");
    private static final List<String> HEADERS = Arrays.asList("Ημερομηνία", "Κωδικός", "Κατάσταση", "Χρέωση",
                                                              "Κατάθεση Τράπεζας",
                                                              "Ημερομηνία Κατάθεσης", "Ποσό Κατάθεσης", "Υπόλοιπο");
    private static final int DATA_HEADER_ROW = 6;
    private static final int DATA_START_ROW = 7;

    /**
     * Generates an Excel reconciliation report for the given customer with the provided data
     * <p>The Excel report includes:</p>
     * <ul>
     *     <li>A title and date range header</li>
     *     <li>Customer information (two rows)</li>
     *     <li>Headers for the data</li>
     *     <li>Sorted reconciliation data rows</li>
     * </ul>
     *
     * @param customer           The {@link Customer} that the report is being generated
     * @param reconciliationRows The {@link List} of {@link ReconciliationRow} containing the data to export
     * @param firstInvoiceDate   The earliest {@link Invoice#getInvoiceDate()} in the report
     * @param lastInvoiceDate    The latest {@link Invoice#getInvoiceDate()} in the report
     * @return A {@link ByteArrayOutputStream} containing the generated Excel file
     * @throws Exception If an error occurs during the Excel generation
     */
    public ByteArrayOutputStream generateReconciliationReport(Customer customer,
                                                              List<ReconciliationRow> reconciliationRows,
                                                              LocalDate firstInvoiceDate,
                                                              LocalDate lastInvoiceDate) throws Exception {
        log.info("Reconciliation report started");

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Reconciliation");

            // Title
            Row reportRow = sheet.createRow(0);
            reportRow.setHeightInPoints(50);
            buildHeader(workbook, reportRow, "Report", 500);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Subtitle - Days range
            Row datesHeaderRow = sheet.createRow(1);
            datesHeaderRow.setHeightInPoints(50);
            String invoicesDaysRange = buildInvoiceDateRange(firstInvoiceDate, lastInvoiceDate);
            buildHeader(workbook, datesHeaderRow, invoicesDaysRange, 200);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Customer information
            Row customerInfoRowOne = sheet.createRow(3);
            Row customerInfoRowTwo = sheet.createRow(4);
            buildCustomerInformation(workbook, sheet, customerInfoRowOne, CUSTOMER_ROW_ONE,
                                     mapCustomerRowOne(customer));
            buildCustomerInformation(workbook, sheet, customerInfoRowTwo, CUSTOMER_ROW_TWO,
                                     mapCustomerRowTwo(customer));

            // Data headers
            Row headersRow = sheet.createRow(DATA_HEADER_ROW);
            buildDataHeaders(workbook, sheet, headersRow, HEADERS);

            // Data
            reconciliationRows.sort(Comparator.comparing(ReconciliationRow::invoiceIssueDate));
            buildData(workbook, sheet, reconciliationRows);

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream;
        }
    }

    // ** Styles **

    /**
     * Creates a {@link CellStyle} with bold font applied
     *
     * @param workbook The {@link Workbook} used to create the font and style
     * @return A {@link CellStyle} configured with bold font
     */
    private static CellStyle boldStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    /**
     * Create a {@link CellStyle} with left alignment on text applied
     *
     * @param workbook The {@link Workbook} used to create the font and style
     * @return A {@link CellStyle} configured with left alignment
     */
    private static CellStyle leftAlignStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    /**
     * Create a {@link CellStyle} with the provided font height, font bold and center alignment in both axis
     *
     * @param workbook   The {@link Workbook} used to create the font and style
     * @param fontHeight The font height of the title
     * @return A {@link CellStyle} with the configured styles
     */
    private static CellStyle titleStyle(Workbook workbook, int fontHeight) {
        Font font = workbook.createFont();
        font.setFontHeight((short) fontHeight);
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * Creates a {@link CellStyle} applying bold font with {@link #boldStyle(Workbook)}, setting a foreground color, and
     * centering the cells
     *
     * @param workbook The {@link Workbook} used to create the font and styles
     * @return A {@link CellStyle} configured with the applied font and styles
     */
    private static CellStyle headerStyle(Workbook workbook) {
        CellStyle style = boldStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * Creates a new {@link CellStyle} by extending a base style with background color
     *
     * @param workbook The {@link Workbook} used to create the style and font
     * @param base     The base {@link CellStyle} to clone
     * @param bgColor  The background color to apply
     * @return A new {@link CellStyle} combining the base style with the colors
     */
    private static CellStyle mergeStyles(Workbook workbook, CellStyle base, IndexedColors bgColor) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(base);
        style.setFillForegroundColor(bgColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        style.setFont(font);
        return style;
    }

    /**
     * Creates a new {@link CellStyle} by extending a base style with decimal number format
     *
     * @param workbook The {@link Workbook} used to create the data format
     * @param base     The base {@link CellStyle} to clone
     * @return A nwe {@link CellStyle} with decimal formatting applied (e.g. 0.00)
     */
    private static CellStyle mergeDecimalStyle(Workbook workbook, CellStyle base) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(base);

        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00")); // 2 decimals
        return style;
    }

    /**
     * Resolves the appropriate {@link CellStyle} based on the invoice status and value type
     * <p>If the provided value is numeric, a number style with decimal format will be returned. Otherwise, a text
     * style is applied. In both cases the style is selected based on the given {@link InvoiceStatus}</p>
     *
     * @param textStyles    A {@link Map} of {@link InvoiceStatus} to text {@link CellStyle}
     * @param numberStyles  A {@link Map} of {@link InvoiceStatus} to numeric {@link CellStyle}
     * @param defaultNumber The fallback numeric {@link CellStyle}
     * @param defaultText   The fallback text {@link CellStyle}
     * @param status        The {@link InvoiceStatus} used to determine styling
     * @param value         The value of the cell used to determine if the style is numeric or not
     * @return The resolved {@link CellStyle} to be applied to the cell
     */
    private static CellStyle getStyle(Map<InvoiceStatus, CellStyle> textStyles,
                                      Map<InvoiceStatus, CellStyle> numberStyles, CellStyle defaultNumber,
                                      CellStyle defaultText, InvoiceStatus status, Object value
    ) {
        boolean isNumeric = value instanceof Number;

        return isNumeric
                ? numberStyles.getOrDefault(status, defaultNumber)
                : textStyles.getOrDefault(status, defaultText);
    }

    // ** Builders **

    /**
     * Creates a header with the provided value
     * <p>Style applied with: {@link #boldStyle(Workbook)}, {@link  #titleStyle(Workbook, int)}</p>
     *
     * @param workbook   The {@link Workbook} used to apply styles
     * @param row        The {@link Row} which the header will be placed
     * @param value      The actual text value of the header
     * @param fontHeight The header's font height
     */
    private static void buildHeader(Workbook workbook, Row row, String value, int fontHeight) {
        Cell cell = row.createCell(0);
        cell.setCellValue(value);
        cell.setCellStyle(boldStyle(workbook));
        cell.setCellStyle(titleStyle(workbook, fontHeight));
    }

    /**
     * Populates a row with customer information as key value pairs adding an extra empty cell between
     * <p>Style applied with: {@link #boldStyle(Workbook)}</p>
     *
     * @param workbook The {@link Workbook} used to apply styles
     * @param sheet    The {@link Sheet} used to auto expand the cells width
     * @param row      The {@link Row} to append the cells
     * @param headers  A {@link List} of headers labels
     * @param values   The corresponding values for each value
     */
    private static void buildCustomerInformation(Workbook workbook, Sheet sheet, Row row, List<String> headers,
                                                 List<Object> values) {
        int counter = 0;

        for (int i = 0; i < headers.size(); i++) {
            String normalizedHeader = headers.get(i).concat(":");

            // Header cell
            Cell headerCell = row.createCell(counter++);
            headerCell.setCellValue(normalizedHeader);
            headerCell.setCellStyle(boldStyle(workbook));
            sheet.autoSizeColumn(i);

            // Value cell
            Cell valueCell = row.createCell(counter++);
            Object value = values.get(i);

            if (value instanceof Number number) {
                valueCell.setCellValue(number.doubleValue());
            } else {
                valueCell.setCellValue(value != null ? value.toString() : "");
            }
            counter++; // extra space
        }
    }

    /**
     * <p>Styles applied with: {@link #headerStyle(Workbook)}</p>
     *
     * @param workbook The {@link Workbook} used to apply styles
     * @param sheet    The {@link Sheet} used to auto expand the cells width
     * @param row      The {@link Row} to append the cells
     * @param headers  The values for the cells
     */
    private static void buildDataHeaders(Workbook workbook, Sheet sheet, Row row, List<String> headers) {
        CellStyle headerStyle = headerStyle(workbook);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Populates the {@link Sheet} with the reconciliation data rows
     * <p>Each row is written into a new Excel row starting from {@link #DATA_START_ROW}</p>
     * <p>This method applies conditional styling based on the {@link InvoiceStatus} on text & numeric fields</p>
     * <p>After the data population all the cells are being auto sized</p>
     *
     * @param workbook           The {@link Workbook} used to create and apply cell styles
     * @param sheet              The {@link Sheet} where the data will be written
     * @param reconciliationRows The {@link List} of {@link ReconciliationRow} containing the data to export
     */
    private static void buildData(Workbook workbook, Sheet sheet, List<ReconciliationRow> reconciliationRows) {
        // Styles
        CellStyle baseStyle = leftAlignStyle(workbook);
        CellStyle decimalStyle = mergeDecimalStyle(workbook, baseStyle);

        Map<InvoiceStatus, CellStyle> textStyles = new HashMap<>();
        Map<InvoiceStatus, CellStyle> numberStyles = new HashMap<>();

        for (InvoiceStatus invoiceStatus : InvoiceStatus.values()) {
            CellStyle textStyle = mergeStyles(workbook, baseStyle, invoiceStatus.getExcelBackgroundColor());
            CellStyle numberStyle = mergeDecimalStyle(workbook, textStyle);
            textStyles.put(invoiceStatus, textStyle);
            numberStyles.put(invoiceStatus, numberStyle);
        }

        // Build rows
        for (int i = 0; i < reconciliationRows.size(); i++) {
            Row row = sheet.createRow(DATA_START_ROW + i);
            ReconciliationRow r = reconciliationRows.get(i);

            createCell(row, 0, formatDate(r.invoiceIssueDate()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.invoiceIssueDate()));

            createCell(row, 1, nonNullString(r.invoiceNumber()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.invoiceNumber()));

            createCell(row, 2, r.invoiceStatus().getGreekTranslate(),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.invoiceStatus()));

            createCell(row, 3, nonNullDouble(r.invoiceAmount()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.invoiceAmount()));

            createCell(row, 4, nonNullString(r.bankName()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(), r.bankName()));

            createCell(row, 5, formatDate(r.depositDate()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(), r.depositDate()));

            createCell(row, 6, nonNullDouble(r.depositAmount()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.depositAmount()));

            createCell(row, 7, nonNullDouble(r.remainingAmount()),
                       getStyle(textStyles, numberStyles, decimalStyle, baseStyle, r.invoiceStatus(),
                                r.remainingAmount()));
        }

        // Expand cells width
        for (int i = 0; i < HEADERS.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ** Mappers & Utils **

    /**
     * Maps the first row of customer information for the report
     *
     * @param customer The {@link Customer} entity containing the data
     * @return A {@link List} of values based on the {@link #CUSTOMER_ROW_ONE} headers
     */
    private static List<Object> mapCustomerRowOne(Customer customer) {
        return List.of(customer.getTin(), customer.getCompanyName(), customer.getCustomerType().getGreekTranslate());
    }

    /**
     * Maps the second row of customer information for the report
     *
     * @param customer The {@link Customer} entity containing the data
     * @return A {@link List} of values based on the {@link #CUSTOMER_ROW_TWO} headers
     */
    private static List<Object> mapCustomerRowTwo(Customer customer) {
        return List.of(
                nonNullString(customer.getLocation()),
                nonNullString(customer.getPhone()),
                nonNullString(customer.getEmail())
        );
    }

    /**
     * Creates and populates a cell with the provided value and applies the provided style
     * <p>Conversion</p>
     * <ul>
     *     <li>Numeric value will be converted to {@code double}</li>
     *     <li>Null value will be an empty {@link String}</li>
     *     <li>Text value will be converted to {@link String}</li>
     * </ul>
     *
     * @param row   The {@link Row} where the cell will be created
     * @param col   The column index of the {@link Cell}
     * @param value The value to set
     * @param style The {@link CellStyle} to apply to the cell
     */
    private static void createCell(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);

        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        cell.setCellStyle(style);
    }

    /**
     * Checks if the provided {@link String} is null or not
     *
     * @param value The string to validate
     * @return The actual value if not null, otherwise an empty {@link String}
     */
    private static String nonNullString(String value) {
        return value == null ? "" : value;
    }

    /**
     * Checks if the provided value is null or not If the value is not null it will be converted to double
     *
     * @param value The value to validate
     * @return The actual value if not null, otherwise {@code 0.00}
     */
    private static double nonNullDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    /**
     * Formats the provided date into {@code dd/MM/YYYY} format
     *
     * @param date The date to be formatted
     * @return The formatted date, otherwise if data is null `-`
     */
    private static String formatDate(LocalDate date) {
        if (date == null) return "-";
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }

    /**
     * Builds the invoice date range using the {@link #formatDate(LocalDate)} to format dates
     * <p>e.g. 14-02-2026 - 03-04-2026</p>
     *
     * @param firstDate The date of the first invoice
     * @param lastDate  The date of the last invoice
     * @return The formatted and build date range from the provided parameters
     */
    private static String buildInvoiceDateRange(LocalDate firstDate, LocalDate lastDate) {
        return formatDate(firstDate) + " - " + formatDate(lastDate);
    }
}
