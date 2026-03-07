package com.manosgrigorakis.logisticsplatform.infrastructure.document;

import com.lowagie.text.pdf.BaseFont;
import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PdfCmrDocumentService {
    @Value("classpath:templates/cmr/index.html")
    private Resource cmrHtmlTemplate;

    @Value("${app.company.name}")
    private String companyName;

    @Value("${app.company.slogan}")
    private String companySlogan;

    @Value("${app.company.location}")
    private String companyLocation;

    @Value("${app.company.phones}")
    private String companyPhones;

    @Value("${app.company.mail}")
    private String companyMail;

    /**
     * Generate a PDF file for the CMR document using the HTML template
     * @param quote The related quote for the CMR
     * @param shipment The related shipment with the CMR
     * @param cmrDocument The actual CMR document model
     * @return The generated PDF in {@link bytes[]}
     */
    public byte[] generateCmrDocumentPdf(Quote quote, Shipment shipment, CmrDocument cmrDocument) {
        try {
            String htmlTemplate = formatTemplate(cmrHtmlTemplate, quote, shipment, cmrDocument);

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
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed generate PDF file", e);
        }
    }

    /**
     * Formats the HTML template with the data from the provided models
     * @param templateFile The actual HTML template
     * @param quote The quote with the data that will be passed to the HTML template
     * @param shipment The shipment with the data that will be passed to the HTML template
     * @param cmrDocument The CMR document with the data that will be passed to the HTML template
     * @return htmlTemplate The formatted HTML template
     * @throws IOException If the template cannot be read or opened
     */
    private String formatTemplate(Resource templateFile, Quote quote, Shipment shipment, CmrDocument cmrDocument)
            throws IOException {
        // Format template
        String htmlTemplate = new String(
                templateFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8
        );

        // Quote
        String quoteNumber = quote.getNumber();
        String origin = quote.getOrigin();
        String destination = quote.getDestination();

        // Customer
        Customer customer = quote.getCustomer();
        String consigneeName = customer.getFullName();
        String consigneeLocation = customer.getLocation();
        String consigneeContact = handleNullFields(customer.getPhone()) + " · " + handleNullFields(customer.getEmail());

        // Shipment
        String shipmentNumber = shipment.getNumber();
        LocalDate pickupDate = shipment.getPickup().toLocalDate();
        String driverName = shipment.getDriver() != null ? shipment.getDriver().fullName() : "-";
        String truckPlate = shipment.getTruck() != null ? shipment.getTruck().getPlate() : "-";
        String truckBrand = shipment.getTruck() != null ? handleNullFields(shipment.getTruck().getBrand()) : "-";
        String trailerPlate = shipment.getTrailer() != null ? shipment.getTrailer().getPlate() : "-";
        String trailerBrand = shipment.getTrailer() != null ? handleNullFields(shipment.getTrailer().getBrand()) : "-";
        String shipmentNotes = handleNullFields(shipment.getNotes());

        // CMR document
        String cmrNumber = cmrDocument.getNumber();
        CmrStatus cmrStatus = cmrDocument.getStatus();
        LocalDate issuedDate = cmrDocument.getCreatedAt().toLocalDate();

        // Format fields
        String formattedPickupDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(pickupDate);
        String formattedIssuedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(issuedDate);

        htmlTemplate = htmlTemplate
                .replace("${companyName}", this.companyName)
                .replace("${companySlogan}", this.companySlogan)
                .replace("${companyLocation}", this.companyLocation)
                .replace("${companyPhones}", this.companyPhones)
                .replace("${companyMail}", this.companyMail)
                .replace("${quoteNumber}", quoteNumber)
                .replace("${origin}", origin)
                .replace("${destination}", destination)
                .replace("${consigneeName}", consigneeName)
                .replace("${consigneeLocation}", consigneeLocation)
                .replace("${consigneeContact}", consigneeContact)
                .replace("${shipmentNumber}", shipmentNumber)
                .replace("${pickupDate}", formattedPickupDate)
                .replace("${driverName}", driverName)
                .replace("${truckPlate}", truckPlate)
                .replace("${truckBrand}", truckBrand)
                .replace("${trailerPlate}", trailerPlate)
                .replace("${trailerBrand}", trailerBrand)
                .replace("${notes}", shipmentNotes)
                .replace("${issueDate}", formattedIssuedDate)
                .replace("${cmrNumber}", cmrNumber)
                .replace("${cmrStatus}", cmrStatus.toString());

        return htmlTemplate;
    }

    /**
     * Handles null fields by replacing null with a '-',
     * preventing for Null Pointer Exception
     * @param field The actual field to be checked for null
     * @return The actual field if not null, otherwise '-'
     */
    private String handleNullFields(String field) {
        return field != null ? field : "-";
    }
}
