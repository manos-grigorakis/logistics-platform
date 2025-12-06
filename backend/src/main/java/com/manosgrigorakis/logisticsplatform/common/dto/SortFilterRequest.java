package com.manosgrigorakis.logisticsplatform.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortFilterRequest {
    @Schema(title = "Field to Sort By",description = "Field to sort by", example = "companyName", defaultValue = "id")
    private String sortBy = "id";

    @Schema(title = "Sort Direction",
            description = "Sort direction",
            example = "desc",
            defaultValue = "asc",
            allowableValues = {"asc", "desc"}
    )
    private String sortDirection = "asc";

    public Sort createSort() {
        return sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }
}
