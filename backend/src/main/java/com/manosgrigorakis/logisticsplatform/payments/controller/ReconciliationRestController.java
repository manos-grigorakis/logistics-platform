package com.manosgrigorakis.logisticsplatform.payments.controller;

import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationProcessResponse;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.service.ReconciliationReportService;
import com.manosgrigorakis.logisticsplatform.payments.service.ReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "Reconciliation")
@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationRestController {
    private final ReconciliationService reconciliationService;
    private final ReconciliationReportService reconciliationReportService;


    @Operation(summary = "Find the Reconciliation Report by Id", description = "Finds the reconciliation report from the given id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation was successfully"),
            @ApiResponse(responseCode = "404", description = "Reconciliation report with the given id not found")
    })
    @GetMapping("/report/{id}")
    public ReconciliationReportResponseDTO get(@PathVariable Long id) {
        return reconciliationReportService.getReconciliationReport(id);
    }

    @Operation(summary = "Reconciliation Process",
            description = "Reconciliation process used to track paid invoices from an invoices file and a bank statement"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reconciliation process successfully operated"),
            @ApiResponse(responseCode = "400", description = """
                    Bad Request. Possible Causes:
                    <ul>
                        <li>Unsupported file extension</li>
                        <li>Failed to process the uploaded files</li>
                        <li>No invoices found in the uploaded file</li>
                    </ul>
                    """
            ),
            @ApiResponse(responseCode = "404", description = "Customer doesn't exist"),
            @ApiResponse(responseCode = "409", description = """
                    Possible Causes:
                    <ul>
                        <li>Customer's TIN doesn't match TIN founded in the file</li>
                        <li>A report already exist with this invoice range</li>
<<<<<<< HEAD
                        <li>Invoices already exist in the system</li>
=======
>>>>>>> 40d49b5 (docs: update controller docs)
                    </ul>
                    """)
    })
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReconciliationProcessResponse reconciliationProcess(@ModelAttribute @Valid ReconciliationRequestDTO dto) {
        return reconciliationService.reconciliationProcess(dto);
    }
}
