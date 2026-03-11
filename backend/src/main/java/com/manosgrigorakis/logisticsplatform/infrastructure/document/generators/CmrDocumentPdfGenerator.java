package com.manosgrigorakis.logisticsplatform.infrastructure.document.generators;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.CmrDocumentPdfRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Component
public final class CmrDocumentPdfGenerator extends BasePdfGenerator<CmrDocumentPdfRequestDTO> {
    @Value("classpath:templates/cmr/index.html")
    private Resource cmrHtmlTemplate;

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
        String cargoItemsRows = buildCargoItemsRows(shipment);

        // CMR document
        String cmrNumber = cmrDocument.getNumber();
        CmrStatus cmrStatus = cmrDocument.getStatus();
        LocalDate issuedDate = cmrDocument.getCreatedAt().toLocalDate();

        // Format fields
        String formattedPickupDate = formatDate(pickupDate);
        String formattedIssuedDate = formatDate(issuedDate);

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
                .replace("${cmrStatus}", cmrStatus.toString());

        return htmlTemplate;
    }

    @Override
    protected Resource getTemplate() {
        return cmrHtmlTemplate;
    }

    /**
     * Builds the HTML table rows for each cargo item
     * @param shipment The shipment which will be used to display its cargo items
     * @return A concatenated {@link Strinng} of table rows
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
}
