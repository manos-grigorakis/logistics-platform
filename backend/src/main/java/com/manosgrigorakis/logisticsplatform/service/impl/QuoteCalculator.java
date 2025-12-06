package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class QuoteCalculator {
    /**
     * Calculate the net total of a Quote
     * The method sums the total price of all related quote item (pre-tax)
     * @param quote The Quote entity containing the related Quote Items
     * @return net total
     */
    public BigDecimal calculateNetTotal(Quote quote) {
        BigDecimal netTotal = BigDecimal.ZERO;

        // Get quote Items
        List<QuoteItem> quoteItems = quote.getQuoteItems();

        // Add each item price
        for(QuoteItem quoteItem : quoteItems) {
            BigDecimal itemPrice = quoteItem.getPrice();
            int quantity = quoteItem.getQuantity();

            if(itemPrice != null) {
                netTotal = netTotal.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        return netTotal.setScale(2, RoundingMode.HALF_UP);
    }
}
