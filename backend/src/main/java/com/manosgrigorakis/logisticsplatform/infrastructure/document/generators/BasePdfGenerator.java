package com.manosgrigorakis.logisticsplatform.infrastructure.document.generators;

import com.lowagie.text.pdf.BaseFont;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.PdfRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class BasePdfGenerator<T extends PdfRequest> {
    @Value("${app.company.name}")
    protected String companyName;

    @Value("${app.company.slogan}")
    protected String companySlogan;

    @Value("${app.company.location}")
    protected String companyLocation;

    @Value("${app.company.phones}")
    protected String companyPhones;

    @Value("${app.company.mail}")
    protected String companyMail;

    @Value("${app.company.website_url}")
    protected String companyWebsiteUrl;

    private final Logger log = LoggerFactory.getLogger(BasePdfGenerator.class);

    /**
     * Generates a PDF document based on the provided request
     * Actions in the Method:
     * <ol>
     *     <li>Gets the HTML template</li>
     *     <li>Formats the HTML template with the data from the request</li>
     *     <li>Parses the formatted HTML template into a {@link Document}</li>
     *     <li>Renders the parsed document into a PDF byte array</li>
     * </ol>
     * @param request The data that will be passed into the final rendered PDF document
     * @return A byte array containing the generated PDF document
     */
    public final byte[] generatePdf(T request) {
        String className = request.getDocumentType();
        String uniqueIdentifier = request.getDocumentIdentifier();

        try {
            String htmlTemplate = formatTemplate(getTemplate(), request);
            Document document = parseHtmlTemplate(htmlTemplate);
            byte[] pdf = renderPdf(document);
            log.info("{} PDF file created with number {}", className, uniqueIdentifier);
            return pdf;
        } catch (Exception e) {
            log.error("Failed to generate {} PDF file with number {}", className, uniqueIdentifier, e);
            throw new RuntimeException("Failed to generate PDF file", e);
        }
    }

    /**
     * Formats the provided HTML template by binding the provided data from the request to the document
     * @param templateFile The HTML template {@link Resource}
     * @param request The request containing the data to be bound
     * @return A formatted HTML {@link String} ready to be rendered to a PDF
     * @throws IOException If the HTML template cannot be read
     */
    protected abstract String formatTemplate(Resource templateFile, T request) throws IOException;

    /**
     * Provides the HTML template resource used for the PDF generation
     * @return The {@link Resource} representing the HTML template
     */
    protected abstract Resource getTemplate();

    /**
     * Renders the provided {@link Document} into a PDF byte array
     * @param document The parsed {@link Document} to be rendered
     * @return A byte array containing the generated PDF document
     * @throws IOException If an error occurs during the PDF creation
     */
    protected byte[] renderPdf(Document document) throws IOException {
            ITextRenderer renderer = new ITextRenderer();
            addFont(renderer);

            // HTML -> PDF
            renderer.setDocumentFromString(document.html());
            renderer.layout();

            // Create the PDF file
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                // Render the final PDF
                renderer.createPDF(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
    }

    /**
     * Add the {@code NotoSans-Regular} font to the provided {@link ITextRenderer},
     * so it can be embedded to the PDF file
     * @param renderer The {@link ITextRenderer} instance responsible for rendering the HTML content.
     * @throws IOException If the font cannot be loaded
     */
    protected void addFont(ITextRenderer renderer) throws IOException {
        ITextFontResolver fontResolver = renderer.getFontResolver();

        URL fontUrl = getClass().getClassLoader().getResource("fonts/NotoSans-Regular.ttf");
        if (fontUrl == null) {
            throw new IllegalStateException("Font file not found in classpath: fonts/NotoSans-Regular.ttf");
        }

        fontResolver.addFont(fontUrl.toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    /**
     * Parses the provided HTML template from a {@link String} to {@link Document}
     * @param htmlTemplate The HTML template to be parsed with {@link Jsoup}
     * @return The HTML template parsed to {@link Document}
     */
    protected Document parseHtmlTemplate(String htmlTemplate) {
        final Document document = Jsoup.parse(htmlTemplate);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document;
    }

    /**
     * Escapes HTML sensitive characters to prevent breaking the HTML structure
     * @param input The raw text
     * @return input Escaped input suitable for the HTML structure
     * or empty {@link String} if input is {@code null}
     */
    protected static String escapeHtml(String input) {
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
     * @param amount An amount (e.g. 50.00)
     * @return amount Formated amount as a string with currency (e.g. 50,00 €)
     */
    protected static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "";
        }

        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        String value = normalized.toPlainString().replace(".", ",");
        return value + " €";
    }

    /**
     * Formats the provided date into {@code dd/MM/YYYY} format
     * @param date The date to be formatted
     * @return The formatted date, otherwise if data is null `-`
     */
    protected String formatDate(LocalDate date) {
        if (date == null) return "-";
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }

    /**
     * Formats the passed parameter into two decimal format, if parameter is {@code null}
     * returns {@code '-'}
     * @param input The input which will be formatted in two decimal format (e.g. 20 -> 20.00)
     * @return The formatted input, or if input is null {@code '-'}
     */
    protected String formatDecimal(BigDecimal input) {
        if (input == null) return "-";

        return String.format("%.2f", input);
    }

    /**
     * Handles null fields by replacing null with a {@code -},
     * preventing for Null Pointer Exception
     * @param field The actual field to be checked for null
     * @return The actual field if not null, otherwise {@code -}
     */
    protected String handleNullFields(String field) {
        return field != null ? field : "-";
    }
}
