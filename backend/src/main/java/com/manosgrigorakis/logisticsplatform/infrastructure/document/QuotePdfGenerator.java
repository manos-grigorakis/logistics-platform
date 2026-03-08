package com.manosgrigorakis.logisticsplatform.infrastructure.document;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.QuotePdfRequestDTO;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.quotes.model.QuoteItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public final class QuotePdfGenerator extends BasePdfGenerator<QuotePdfRequestDTO>  {
    @Value("classpath:templates/quotes/greek/index.html")
    private Resource greekQuoteHtmlTemplate;

    @Value("${app.company.representative}")
    private String companyRepresentative;

    @Value("${app.company.representative_title}")
    private String companyRepresentativeTitle;

    @Override
    protected String formatTemplate(Resource templateFile, QuotePdfRequestDTO request) throws IOException {
        Quote quote = request.quote();

        // Format template
        String htmlTemplate = new String(
                templateFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8
        );

        String attn = quote.getCustomer().getFirstName() + " " + quote.getCustomer().getLastName();
        String itemsRows = buildItemsRows(quote);
        String specialTerms = buildSpecialTerms(quote);
        String notes = buildNotes(quote);

        // Format issueDate
        String formatedDate = formatDate(quote.getIssueDate());

        htmlTemplate = htmlTemplate
                .replace("${companyName}", this.companyName)
                .replace("${companySlogan}", this.companySlogan)
                .replace("${companyLocation}", this.companyLocation)
                .replace("${companyPhones}", this.companyPhones)
                .replace("${companyMail}", this.companyMail)
                .replace("${companyWebsiteUrl}", this.companyWebsiteUrl)
                .replace("${companyRepresentative}", this.companyRepresentative)
                .replace("${companyRepresentativeTitle}", this.companyRepresentativeTitle)
                .replace("${quoteNumber}", quote.getNumber())
                .replace("${issueDate}", formatedDate)
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

    @Override
    protected Resource getTemplate() {
        return this.greekQuoteHtmlTemplate;
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
            String quantity = item.getQuantity().toString();
            String unit = item.getUnit().getDisplayName("el");
            String price = formatMoney(item.getPrice());

            rows.append("<tr class=\"price-row\">")
                    .append("<td class=\"nowrap\">").append(escapeHtml(name)).append("</td>")
                    .append("<td>").append(escapeHtml(description)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(quantity)).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(escapeHtml(unit)).append("</td>")
                    .append("<td class=\"text-right\">").append(escapeHtml(price)).append("</td>")
                    .append("</tr>");
        }

        return rows.toString();
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
        if (terms == null || terms.trim().isEmpty() || terms.equals("<p></p>")) {
            return "<ul><li>Συμπληρώνονται ανάλογα με το έργο.</li></ul>";
        }

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
        if (notes == null || notes.trim().isEmpty() || notes.equals("<p></p>")) {
            return "Δεν υπάρχουν επιπλέον σημειώσεις.";
        }

        return notes;
    }
}
