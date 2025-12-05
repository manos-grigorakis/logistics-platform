package com.manosgrigorakis.logisticsplatform.filters;

import com.manosgrigorakis.logisticsplatform.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilterRequest {
    @Size(min = 9, max = 9)
    @Schema(title = "TIN", description = "Tax Identification Number (9 digits)", example = "123456789")
    private String tin;

    @Size(max = 80)
    @Schema(title = "Company Name", description = "Company name", example = "ACME Corp")
    private String companyName;

    @Schema(title = "Customer Type", description = "Customer type")
    private CustomerType customerType;
}
