package com.manosgrigorakis.logisticsplatform.payments.controller;

import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoices")
public class InvoiceRestController {
    private final InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Bulk Invoices Import via Excel", description = "Bulk invoices upload operation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bulk invoices operation was successfully"),
            @ApiResponse(responseCode = "400", description = """
                    Bad Request. Possible Causes:
                    <ul>
                        <li>Unsupported file extension</li>
                        <li>Failed to process the uploaded file</li>
                    </ul>
                    """
            ),
            @ApiResponse(responseCode = "404", description = "Customer doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Customer's TIN doesn't match TIN founded in the file")
    })
    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BulkInvoiceResponseDTO bulkInvoicesImport(@ModelAttribute @Valid BulkInvoiceRequestDTO dto) {
        return this.invoiceService.bulkInvoicesImport(dto);
    }
}
