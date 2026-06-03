package com.manosgrigorakis.logisticsplatform.quotes.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.QuoteFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quote.*;
import com.manosgrigorakis.logisticsplatform.quotes.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/v1/quotes")
@Tag(name = "Quotes", description =
        "Create, Read, Update operations on Quotes with PDF generation, presigned URL for preview and storing on S3/Minio"
)
public class QuoteRestController {
    private final QuoteService quoteService;

    public QuoteRestController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Operation(summary = "Get all Quotes", description = "Lists all quotes with pagination")
    @ApiResponse(responseCode = "200", description = "List of quotes with pagination")
    @GetMapping()
    public ApiResponseWrapper<Page<QuoteListResponseDTO>> getAllQuotes(
            @ParameterObject  @ModelAttribute @Valid QuoteFilterRequest quoteFilter,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest page,
            @ParameterObject @ModelAttribute SortFilterRequest sort
            )
    {
        return new ApiResponseWrapper<>(quoteService.getAllQuotes(quoteFilter, page, sort));
    }

    @Operation(summary = "Get Quote by Id", description = "Find quote by id and generates presigned preview URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded quote"),
            @ApiResponse(responseCode = "404", description = "Quote doesn't exist"),
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<QuoteResponseDTO> getQuoteById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(quoteService.getQuoteById(id));
    }

    @Operation(summary = "Create a Quote", description = """
            Creates a new quote with the following processes:
            - Generates unique quote number
            - Calculates total net, VAT, amount and gross price
            - Generates PDF file from an HTML template
            - Uploads PDF file to S3/Minio storage
            - Creates a presigned URL for preview
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "404", description = "User doesn't exists | Customer doesn't exists"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ApiResponseWrapper<QuoteCreatedResponseDTO> createQuote(@RequestBody @Valid QuoteRequestDTO dto) {
        return new ApiResponseWrapper<>(quoteService.createQuote(dto));
    }

    @Operation(summary = "Update Quote by Id", description = "Update a quote by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated quote"),
            @ApiResponse(responseCode = "404", description = "User doesn't exists | Customer doesn't exists"),
            @ApiResponse(responseCode = "409", description = "Quote is not editable due to status"),

    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<QuoteResponseDTO> updateQuoteById(@PathVariable Long id,
                                                                @RequestBody @Valid QuoteUpdateRequestDTO dto) {
        return new ApiResponseWrapper<>(quoteService.updateQuote(id, dto));
    }

    @Operation(summary = "Update Quote Status by Id", description = "Update a quote status by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Updated quote"),
            @ApiResponse(responseCode = "404", description = "Quote doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Quote is not editable due to status"),

    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/status")
    public void updateQuoteStatus(@PathVariable Long id, @RequestBody @Valid UpdateQuoteStatusRequestDTO dto) {
        quoteService.updateQuoteStatus(id, dto);
    }
}
