package com.manosgrigorakis.logisticsplatform.customers.specs;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecs {
    public static Specification<Customer> likeTin(String tin) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("tin"), "%" + tin + "%");
    }

    public static Specification<Customer> likeCompanyName(String companyName) {
        return (root, query, criteriaBuilder) ->
             criteriaBuilder.like(root.get("companyName"),"%" + companyName.toLowerCase() + "%");
    }

    public static Specification<Customer> equalCustomerType(CustomerType customerType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customerType"), customerType);
    }
}
