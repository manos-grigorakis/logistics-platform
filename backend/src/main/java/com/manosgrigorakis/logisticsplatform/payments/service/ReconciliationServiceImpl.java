package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ReconciliationRow;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ExcelBankTransactionReaderNgb;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ReconciliationReportExcelGenerator;
import com.manosgrigorakis.logisticsplatform.payments.dto.*;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.mapper.BankTransactionMapper;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.model.InvoicePayments;
import com.manosgrigorakis.logisticsplatform.payments.repository.BankTransactionRepository;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoicePaymentsRepository;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoiceRepository;
import com.manosgrigorakis.logisticsplatform.payments.utility.ReconciliationEngine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    private final InvoiceService invoiceService;
    private final ExcelBankTransactionReaderNgb excelBankTransactionReaderNgb;
    private final InvoiceRepository invoiceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final InvoicePaymentsRepository invoicePaymentsRepository;
    private final ReconciliationReportExcelGenerator reconciliationReportExcelGenerator;
    private final ReconciliationReportService reconciliationReportService;
    private final AuditService auditService;

    @Override
    @Transactional
    public ReconciliationProcessResponse reconciliationProcess(ReconciliationRequestDTO dto) {
        List<BankTransaction> noMatchTransaction = new ArrayList<>();

        PrepareReconciliationResult invoicesResult =
                this.invoiceService.prepareInvoicesForReconciliation(dto.getCustomerId(), dto.getInvoiceFile());

        Customer customer = invoicesResult.customer();

        // Validate and process bank statement file
        validateFileType(dto.getBankStatementFile());
        List<BankTransaction> bankTransactions = new ArrayList<>(processExcelFile(dto.getBankStatementFile()));

        // Filter transactions based on the customer name
        List<BankTransaction> filteredBankTransactions = bankTransactions.stream()
                .filter(t -> matchesCustomer(t, customer)).toList();

        // 1. Transaction contains one single invoice number in description
        InvoiceMatchingResults singleInvoiceMatchResult = ReconciliationEngine
                .invoiceNumberDeclared(invoicesResult.invoices(), filteredBankTransactions);

        List<Invoice> matchedInvoices = new ArrayList<>(singleInvoiceMatchResult.matchedInvoices());
        List<Invoice> noMatchInvoices = new ArrayList<>(singleInvoiceMatchResult.noMatchInvoices());
        List<BankTransaction> matchedTransactions = new ArrayList<>(singleInvoiceMatchResult.matchedTransactions());
        List<InvoicePayments> invoicePayments = new ArrayList<>(singleInvoiceMatchResult.invoicePayments());

        // Update list with transactions that not already exists in matched transactions
        filteredBankTransactions.stream()
                .filter(t -> !matchedTransactions.contains(t))
                .forEach(noMatchTransaction::add);

        // 2. One transaction PAYS multiple invoices by defining the invoices numbers in transaction description
        MultipleInvoicesMatchingResults multipleDeclaredInvoices = ReconciliationEngine
                .multipleInvoicesDeclared(noMatchInvoices, noMatchTransaction);

        matchedInvoices.addAll(multipleDeclaredInvoices.matchedInvoices());
        matchedTransactions.addAll(multipleDeclaredInvoices.matchedTransactions());
        invoicePayments.addAll(multipleDeclaredInvoices.invoicePayments());

        // Remove founded transaction & invoices
        noMatchTransaction.removeIf(t -> multipleDeclaredInvoices.matchedTransactions().contains(t));
        noMatchInvoices.removeIf(i -> multipleDeclaredInvoices.matchedInvoices().contains(i));

        // 3. One transaction pays multiple invoices
        MultipleInvoicesMatchingResults multipleInvoicesMatchResult = ReconciliationEngine
                .multipleInvoices(noMatchTransaction, noMatchInvoices);

        matchedInvoices.addAll(multipleInvoicesMatchResult.matchedInvoices());
        matchedTransactions.addAll(multipleInvoicesMatchResult.matchedTransactions());
        invoicePayments.addAll(multipleInvoicesMatchResult.invoicePayments());

        noMatchTransaction.removeIf(t -> multipleInvoicesMatchResult.matchedTransactions().contains(t));
        noMatchInvoices.removeIf(i -> multipleInvoicesMatchResult.matchedInvoices().contains(i));

        List<ReconciliationRow> reconciliationRows = buildReconciliationRows(invoicePayments, noMatchInvoices);
        LocalDate firstInvoiceDate = getFirstInvoiceDate(invoicesResult.invoices());
        LocalDate lastInvoiceDate = getLastInvoiceDate(invoicesResult.invoices());

        ByteArrayOutputStream report;

        try {
            report = reconciliationReportExcelGenerator.generateReconciliationReport(customer, reconciliationRows,
                                                                                     firstInvoiceDate, lastInvoiceDate);
        } catch (IOException e) {
            log.error("Failed to generate reconciliation report for customer {}", customer.getCompanyName(), e);
            throw new RuntimeException("Failed to generate reconciliation Excel report", e);
        }

        ReconciliationReportCreateResponseDTO reportResponse = reconciliationReportService.createReconciliationReport(
                new CreateReconciliationReport(report, firstInvoiceDate, lastInvoiceDate, matchedInvoices.size(),
                                               noMatchInvoices.size(), customer));
        invoiceRepository.saveAll(invoicesResult.invoices());
        bankTransactionRepository.saveAll(matchedTransactions);
        invoicePaymentsRepository.saveAll(invoicePayments);

        log.info("Reconciliation process successfully finished for customer id: {}", dto.getCustomerId());

        Set<Invoice> uniqueInvoices = new LinkedHashSet<>(invoicesResult.invoices());
        List<Invoice> paidInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.PAID);
        List<Invoice> partiallyPaidInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.PARTIALLY_PAID);
        List<Invoice> outstandingInvoices = filterInvoicesByStatus(invoicesResult.invoices(),
                                                                   InvoiceStatus.OUTSTANDING);
        List<Invoice> disputedInvoices = filterInvoicesByStatus(invoicesResult.invoices(), InvoiceStatus.DISPUTED);

        logReconciliationProcess(customer.getId(), matchedTransactions.size(), matchedInvoices.size(),
                                 paidInvoices.size(), outstandingInvoices.size(), partiallyPaidInvoices.size(),
                                 disputedInvoices.size());

        return new ReconciliationProcessResponse(
                uniqueInvoices.size(),
                matchedInvoices.size(),
                noMatchInvoices.size(),
                matchedTransactions.size(),
                noMatchTransaction.size(),
                paidInvoices.size(),
                partiallyPaidInvoices.size(),
                outstandingInvoices.size(),
                disputedInvoices.size(),
                reportResponse
        );
    }

    /**
     * Processes the uploaded bank statement Excel file using
     * {@link #excelBankTransactionReaderNgb#processExcelFile(MultipartFile)}
     *
     * @param bankStatement The bank statement Excel to process
     * @return A list containing the parsed bank transactions from the Excel file
     */
    private List<BankTransaction> processExcelFile(MultipartFile bankStatement) {
        try {
            List<BankStatementImportResultDTO> bankResults = excelBankTransactionReaderNgb.readExcel(bankStatement);
            return bankResults.stream().map(result ->
                                                    BankTransactionMapper.toEntity(result, "NBG")).toList();
        } catch (IOException e) {
            log.error("Failed to process bank statement for {}", bankStatement.getOriginalFilename(), e);
            throw new BadRequestException("Processing Excel bank statement failed", "FAILED_TO_PROCESS_BANK");
        }
    }

    /**
     * Checks if the {@code SenderName} from the {@link BankTransaction} matches the {@link Customer}
     *
     * @param transaction The {@link BankTransaction} to check
     * @param customer    The {@link Customer} used for matching
     * @return {@code true} If the {@code SenderName} matches with the {@link Customer}, otherwise {@code false}
     */
    private boolean matchesCustomer(BankTransaction transaction, Customer customer) {
        if (transaction.getSenderName() == null) return false;

        String senderName = transaction.getSenderName().toUpperCase();

        if (customer.getCustomerType() == CustomerType.COMPANY) {
            return senderName.contains(customer.getCompanyName().toUpperCase());
        }

        String customerName = customer.getLastName().toUpperCase() + " " + customer.getFirstName().toUpperCase();
        return senderName.contains(customerName);
    }

    /**
     * Returns a set of {@link Invoice} filtered by status
     *
     * @param invoices The invoices to filter
     * @param status   The {@link InvoiceStatus} to filter the invoices
     * @return A filtered {@link List} of {@link Invoice} by status
     */
    private List<Invoice> filterInvoicesByStatus(List<Invoice> invoices, InvoiceStatus status) {
        return invoices.stream().filter(i -> Objects.equals(i.getStatus(), status)).toList();
    }

    /**
     * Validates that the provided {@code file} has the {@code .xlsx} extension
     *
     * @param file The file which will be validated
     * @throws BadRequestException If the file extension is invalid
     */
    private void validateFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.endsWith(".xlsx")) {
            throw new BadRequestException("Only .xlsx files are support", "UNSUPPORTED_FILE");
        }
    }

    /**
     * Builds reconciliation rows by mapping the relationships between {@link InvoicePayments}, {@link Invoice} and
     * {@link BankTransaction} into {@link ReconciliationRow}
     * <p>Includes both matched & unmatched invoices</p>
     * <ul>
     *     <li>Matched invoices are appended along with their relational data</li>
     *     <li>Unmatched invoices are appended without any transaction data</li>
     * </ul>
     *
     * @param invoicePayments   The {@link List} of the {@link InvoicePayments} entities to be used to build the report
     *                          rows
     * @param unmatchedInvoices The {@link List} of the unmatched invoices to populate the report
     * @return A list of {@link ReconciliationRow} ready for further processing (e.g. Excel generation)
     */
    private List<ReconciliationRow> buildReconciliationRows(List<InvoicePayments> invoicePayments,
                                                            List<Invoice> unmatchedInvoices) {
        List<ReconciliationRow> reconciliationRows = new ArrayList<>();

        for (InvoicePayments invoicePayment : invoicePayments) {
            BankTransaction transaction = invoicePayment.getBankTransaction();
            Invoice invoice = invoicePayment.getInvoice();
            ReconciliationRow row = new ReconciliationRow(
                    invoice.getInvoiceDate(),
                    invoice.getExternalInvoiceNumber(),
                    invoice.getStatus(),
                    invoice.getTotalAmount(),
                    transaction.getBankName(),
                    transaction.getIssueDate(),
                    invoicePayment.getAmount(),
                    invoice.getRemainingAmount()
            );

            reconciliationRows.add(row);
        }

        for (Invoice unmatchedInvoice : unmatchedInvoices) {
            ReconciliationRow row = new ReconciliationRow(
                    unmatchedInvoice.getInvoiceDate(),
                    unmatchedInvoice.getExternalInvoiceNumber(),
                    unmatchedInvoice.getStatus(),
                    unmatchedInvoice.getTotalAmount(),
                    "",
                    null,
                    BigDecimal.ZERO,
                    unmatchedInvoice.getRemainingAmount()
            );

            reconciliationRows.add(row);
        }

        return reconciliationRows;
    }

    /**
     * Finds from the provided {@link List} of {@link Invoice} the first invoice based on the issue date
     *
     * @param invoices The invoices {@link List} to be filtered
     * @return The date of the first invoice after processing
     */
    private LocalDate getFirstInvoiceDate(List<Invoice> invoices) {
        return invoices.stream()
                .map(Invoice::getInvoiceDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    /**
     * Finds from the provided {@link List} of {@link Invoice} the last invoice based on the issue date
     *
     * @param invoices The invoices {@link List} to be filtered
     * @return The date of the last invoice after processing
     */
    private LocalDate getLastInvoiceDate(List<Invoice> invoices) {
        return invoices.stream()
                .map(Invoice::getInvoiceDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    private void logReconciliationProcess(Long customerId, int matchedBankTransactions, int matchedInvoices,
                                          int paidInvoices, int outstandingInvoices, int partiallyPaidInvoices,
                                          int disputedInvoices) {
        auditService.log(
                AuditEventDTO.builder()
                        .entityType("Reconciliation Process")
                        .notes(
                                "Customer ID: " + customerId
                                        + " | Matched Bank Transactions: " + matchedBankTransactions
                                        + " | Matched Invoices: " + matchedInvoices
                                        + " | Paid Invoices: " + paidInvoices
                                        + " | Outstanding Invoices: " + outstandingInvoices
                                        + " | Partially Paid Invoices: " + partiallyPaidInvoices
                                        + " | DisputedInvoices: " + disputedInvoices
                        )
                        .action(AuditAction.CREATE)
                        .build()
        );
    }
}
