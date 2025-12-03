package com.manosgrigorakis.logisticsplatform.service.impl;

import com.lowagie.text.pdf.BaseFont;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class PdfService {
    @Value("classpath:templates/quotes/greek/index.html")
    private Resource greekQuoteHtmlTemplate;
    private final Logger log = LoggerFactory.getLogger(PdfService.class);

    /**
     * Generate a PDF file for the given Quote using an HTML template
     *
     * @param quote The quote that injects data to the HTML template
     * @return The generated PDF in bytes
     */
    public byte[] generateQuotePdf(Quote quote) {
        try {
            String htmlTemplate = formatTemplate(greekQuoteHtmlTemplate, quote);

            final Document document = Jsoup.parse(htmlTemplate);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();

            // Add font
            URL fontUrl = getClass().getClassLoader().getResource("fonts/NotoSans-Regular.ttf");
            if (fontUrl == null) {
                throw new IllegalStateException("Font file not found in classpath: fonts/NotoSans-Regular.ttf");
            }

            fontResolver.addFont(fontUrl.toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // HTML -> PDF
            renderer.setDocumentFromString(document.html());
            renderer.layout();

            // Create the PDF file
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                // Render the final PDF
                renderer.createPDF(byteArrayOutputStream);
                log.info("Quote PDF file created with number {}", quote.getNumber());
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            log.error("Failed to generate quote PDF file with number {}", quote.getNumber());
            throw new RuntimeException("Failed generate PDF file", e);
        }
    }

    /**
     * Formats an HTML template with the data from the provided Quote
     *
     * @param templateFile The actual HTML template
     * @param quote        The Quote that contains the data to build the HTML template
     * @return htmlTemplate The formated HTML template
     * @throws IOException If the template cannot be read or opened
     */
    private static String formatTemplate(Resource templateFile, Quote quote) throws IOException {
        // Format template
        String htmlTemplate = new String(
                templateFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8
        );

        String attn = quote.getCustomer().getFirstName() + " " + quote.getCustomer().getLastName();
        String itemsRows = buildItemsRows(quote);
        String specialTerms = buildSpecialTerms(quote);
        String notes = buildNotes(quote);

        htmlTemplate = htmlTemplate
                .replace("${quoteNumber}", quote.getNumber())
                .replace("${issueDate}", quote.getIssueDate().toString())
                .replace("${company}", quote.getCustomer().getCompanyName())
                .replace("${attn}", attn)
                .replace("${origin}", quote.getOrigin())
                .replace("${destination}", quote.getDestination())
                .replace("${itemsRows}", itemsRows)
                .replace("${netPrice}", formatMoney(quote.getNetPrice()))
                .replace("${vatAmount}", formatMoney(quote.getVatAmount()))
                .replace("${grossPrice}", formatMoney(quote.getGrossPrice()))
                .replace("${taxRatePercentage}", quote.getTaxRatePercentage().toString())
                .replace("${notes}", notes)
                .replace("${validityDate}", quote.getValidityDays().toString())
                .replace("${specialTerms}", specialTerms);

        return htmlTemplate;
    }

    /**
     * Builds the HTML table rows for each quote item row
     *
     * @param quote The quote containing the quote items
     * @return string A concatenated string of table rows
     */
    private static String buildItemsRows(Quote quote) {
        StringBuilder rows = new StringBuilder();

        for (QuoteItem item : quote.getQuoteItems()) {
            String name = item.getName();
            String description = item.getDescription();
            String price = formatMoney(item.getPrice());

            rows.append("<tr class=\"price-row\">")
                    .append("<td>").append(escapeHtml(name)).append("</td>")
                    .append("<td>").append(escapeHtml(description)).append("</td>")
                    .append("<td class=\"text-right\">").append(escapeHtml(price)).append("</td>")
                    .append("</tr>");
        }

        return rows.toString();
    }

    /**
     * Escapes HTML sensitive characters to prevent breaking the HTML structure
     *
     * @param input The raw text
     * @return input Escaped input suitable for the HTML structure
     */
    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Format the amount to Greek format
     * and adding the currency
     *
     * @param amount An amount (e.g. 50.00)
     * @return amount Formated amount as a string with currency (e.g. 50,00 €)
     */
    private static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "";
        }

        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        String value = normalized.toPlainString().replace(".", ",");
        return value + " €";
    }

    /**
     * Builds the special terms for the HTML template
     *
     * @param quote The Quote containing the special terms
     * @return terms Escaped terms safe for HTML structure
     */
    private static String buildSpecialTerms(Quote quote) {
        String terms = quote.getSpecialTerms();

        // Case: no special terms provided
        if (terms == null || terms.trim().isEmpty()) {
            return "Συμπληρώνονται ανάλογα με το έργο.";
        }

        terms = escapeHtml(terms);

        // Convert line breaks to <br/> for PDF
        terms = terms.replace("\n", "<br/>");

        return terms;
    }

    /**
     * Builds the notes for the HTML template
     * escapes the data and keeps the line breaks
     *
     * @param quote The Quote containing the special terms
     * @return notes Escaped notes safe for HTML structure
     */
    private static String buildNotes(Quote quote) {
        String notes = quote.getNotes();

        // Case: no notes provided
        if (notes == null || notes.trim().isEmpty()) {
            return "Δεν υπάρχουν επιπλέον σημειώσεις.";
        }

        // Escape and keep line breaks
        notes = escapeHtml(notes);
        notes = notes.replace("\n", "<br/>");

        return notes;
    }
}
