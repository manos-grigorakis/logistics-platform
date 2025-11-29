package com.manosgrigorakis.logisticsplatform.filters;

import com.manosgrigorakis.logisticsplatform.enums.CustomerType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilterRequest {
    @Size(min = 9, max = 9)
    private String tin;

    @Size(max = 80)
    private String companyName;

    private CustomerType customerType;
}
