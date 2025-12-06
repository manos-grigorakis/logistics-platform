package com.manosgrigorakis.logisticsplatform.quotes.specs;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


public class QuotesSpecs {
    public static Specification<Quote> likeNumber(String number) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("number"), "%" + number + "%");
    }

    public static Specification<Quote> likeCompanyName(String companyName) {
        return (root, query, criteriaBuilder) -> {
            Join<Quote, Customer> customerJoin = root.join("customer");
            return criteriaBuilder.like(customerJoin.get("companyName"), "%" + companyName + "%");
        };
    }

    public static Specification<Quote> equalQuoteStatus(QuoteStatus quoteStatus) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("quoteStatus"), quoteStatus);
    }
}
