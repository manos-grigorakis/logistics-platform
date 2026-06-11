package com.manosgrigorakis.logisticsplatform.infrastructure.document.generators;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.CmrDocumentPdfRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public final class CmrDocumentPdfGenerator extends BasePdfGenerator<CmrDocumentPdfRequestDTO> {
    @Value("classpath:templates/cmr/index.html")
    private Resource cmrHtmlTemplate;

    /**
     * Generates all four CMR document copies and merges them into a single PDF file
     *
     * @param request The request containing all the mandatory information for the PDF
     * @return A {@code byte[]} containing the merged CMR document copies
     * @throws IOException If the PDF generation or merge operation fails
     */
    public byte[] renderAllCopies(CmrDocumentPdfRequestDTO request) throws IOException {
        String htmlTemplate = formatTemplate(cmrHtmlTemplate, request);
        List<byte[]> files = highlightAndRenderPdfCopies(htmlTemplate);
        return mergeHighlightedPdfCopies(files);
    }

    @Override
    protected String formatTemplate(Resource templateFile, CmrDocumentPdfRequestDTO request) throws IOException {
        Quote quote = request.quote();
        Shipment shipment = request.shipment();
        CmrDocument cmrDocument = request.cmrDocument();

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
        String consigneeLocation = handleNullFields(customer.getLocation());
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
        String cargoItemsRows = buildCargoItemsRows(shipment);

        // CMR document
        String cmrNumber = cmrDocument.getNumber();
        CmrStatus cmrStatus = cmrDocument.getStatus();
        LocalDate issuedDate = cmrDocument.getCreatedAt().toLocalDate();

        // Format fields
        String formattedPickupDate = formatDate(pickupDate);
        String formattedIssuedDate = formatDate(issuedDate);
        String formatedQrCode = formatQrCode(generateQrCode(cmrNumber));

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
                .replace("${cargoRows}", cargoItemsRows)
                .replace("${issueDate}", formattedIssuedDate)
                .replace("${cmrNumber}", cmrNumber)
                .replace("${cmrStatus}", cmrStatus.toString())
                .replace("${cmrStatusBadgeClass}", applyStatusBadgeColor(cmrStatus))
                .replace("${qrCode}", formatedQrCode);

        return htmlTemplate;
    }

    @Override
    protected Resource getTemplate() {
        return cmrHtmlTemplate;
    }

    /**
     * Builds the HTML table rows for each cargo item
     * @param shipment The shipment which will be used to display its cargo items
     * @return A concatenated {@link String} of table rows
     */
    private String buildCargoItemsRows(Shipment shipment) {
        StringBuilder rows = new StringBuilder();

        for (ShipmentCargo item : shipment.getShipmentCargos()) {
            String description = item.getDescription();
            String quantity = item.getQuantity().toString();
            String unit = item.getUnit().toString();
            String weightKg = formatDecimal(item.getWeightKg());
            String volumeM3 = handleNullFields(formatDecimal(item.getVolumeM3()));

            rows.append("<tr>")
                    .append("<td class=\"nowrap\">").append(escapeHtml(description)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(quantity)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(unit)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(weightKg)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(volumeM3)).append("</td>")
                    .append("</tr>");
        }
        return rows.toString();
    }

    /**
     * Applies CSS class for CMR Document status badge
     *
     * @param status The CMR Document
     * @return The CSS class that will be applied in the template
     */
    private String applyStatusBadgeColor(CmrStatus status) {
        if(status.equals(CmrStatus.GENERATED)) return "status-badge-generated";
        else if (status.equals(CmrStatus.SIGNED)) return "status-badge-signed";
        else if (status.equals(CmrStatus.CANCELLED)) return "status-badge-cancelled";
        else return "status-badge-fallback";
    }

    /**
     * Generates a QR Code (PNG format) for the given CMR document number
     *
     * @param cmrNumber The CMR number to be used inside the QR Code
     * @return A {@code Base64} encoded PNG image containing the QR Code
     * @throws IOException If the QR Code image cannot be written to the output stream
     * @throws RuntimeException If the QR code generation fails
     */
    private String generateQrCode(String cmrNumber) throws IOException {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(cmrNumber, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "png", outputStream);
                byte[] data = outputStream.toByteArray();
                return Base64.getEncoder().encodeToString(data);
            }
        } catch (WriterException e) {
            log.warn("Failed to encode QR code", e);
            throw new RuntimeException("Failed to generate QR Code for CMR number: " + cmrNumber, e);
        }
    }

    /**
     * Formats a {@code Base64} encoded QR Code image so it can be rendered in an HTML {@code <img>} tag element
     *
     * @param qrCode The {@code Base64} encoded QR code image
     * @return A data URI following the prefix: {@code data:image/png;base64}
     */
    private String formatQrCode(String qrCode) {
        return "data:image/png;base64," + qrCode;
    }

    /**
     * Generates all CMR document copies by highlighting for each rendered PDF copy only one copy section
     * in the footer of the template
     *
     * @param html The processed HTML template to be used
     * @return A list containing the rendered PDFs copies
     * @throws IOException If the render PDF operation fails
     */
    private List<byte[]> highlightAndRenderPdfCopies(String html) throws IOException {
        Document document = Jsoup.parse(html);
        Element element = document.getElementById("copies-rows");

        if(element == null) {
            throw new IllegalStateException("No copies rows element found for rendering CMR document copies");
        }

        Elements elements = element.getElementsByTag("td");
        List<byte[]> pdfCopies = new ArrayList<>();

        for (Element tr : elements) {
            // Remove all active classes from elements
            elements.forEach(e -> e.removeClass("active"));

            tr.addClass("active");
            Document parsedDocument = parseHtmlTemplate(document.html());
            pdfCopies.add(renderPdf(parsedDocument));
        }
        return pdfCopies;
    }

    /**
     * Merges the provided PDFs files into a single one
     *
     * @param files The PDF file to be merged
     * @return The merged PDFs in a {@code byte[]}
     * @throws IOException If the outputStream operation fails
     */
    private byte[] mergeHighlightedPdfCopies(List<byte[]> files) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationStream(outputStream);

            // Merge PDFs
            for (byte[] file : files) {
                merger.addSource(new RandomAccessReadBuffer(file));
            }

            merger.mergeDocuments(null);
            return outputStream.toByteArray();
        }
    }
}
