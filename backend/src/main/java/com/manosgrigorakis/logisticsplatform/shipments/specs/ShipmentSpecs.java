package com.manosgrigorakis.logisticsplatform.shipments.specs;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ShipmentSpecs {
    public static Specification<Shipment> likeNumber(String number) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("number"), "%" + number + "%");
    }

    public static Specification<Shipment> equalStatus(ShipmentStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Shipment> pickupBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) ->
        {
            if(from != null && to != null) {
                return criteriaBuilder.between(root.get("pickup"), from, to);
            }

            if(from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("pickup"), from);
            }

            if(to != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("pickup"), to);
            }

            // No filter
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Shipment> equalByDriverId(Long id) {
        return (root, query, criteriaBuilder) -> {
            Join<Shipment, User> joinDriver = root.join("driver");
            return criteriaBuilder.equal(joinDriver.get("id"), id);
        };
    }
}
