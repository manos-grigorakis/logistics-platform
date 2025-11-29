package com.manosgrigorakis.logisticsplatform.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortFilterRequest {
    private String sortBy = "id";
    private String sortDirection = "asc";

    public Sort createSort() {
        return sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }
}
