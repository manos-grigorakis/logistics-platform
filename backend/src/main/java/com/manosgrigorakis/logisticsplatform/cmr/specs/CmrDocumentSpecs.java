package com.manosgrigorakis.logisticsplatform.cmr.specs;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import org.springframework.data.jpa.domain.Specification;

public class CmrDocumentSpecs {
    public static Specification<CmrDocument> likeNumber(String number) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("number"), "%" + number + "%");
    }

    public static Specification<CmrDocument> equalCmrDocumentStatus(CmrStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
}
