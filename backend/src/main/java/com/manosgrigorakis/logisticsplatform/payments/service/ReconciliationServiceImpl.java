package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ExcelBankTransactionReaderNgb;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    private final InvoiceService invoiceService;
    private final ExcelBankTransactionReaderNgb excelBankTransactionReaderNgb;
    private final InvoiceRepository invoiceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final InvoicePaymentsRepository invoicePaymentsRepository;

    @Override
    @Transactional
    public ReconciliationProcessResponse reconciliationProcess(ReconciliationRequestDTO dto) {
        List<BankTransaction> noMatchTransaction = new ArrayList<>();

        PrepareReconciliationResult invoicesResult =
                this.invoiceService.prepareInvoicesForReconciliation(dto.getCustomerId(), dto.getInvoiceFile());

        Customer customer = invoicesResult.customer();

        // Validate and process bank statement file
        validateFileType(dto.getBankStatement());
        List<BankTransaction> bankTransactions = new ArrayList<>(processExcelFile(dto.getBankStatement()));

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

        log.info("Matched invoices size: {} | Unique: {}", matchedInvoices.size(), new HashSet<>(matchedInvoices).size());

        invoiceRepository.saveAll(matchedInvoices);
        bankTransactionRepository.saveAll(matchedTransactions);
        invoicePaymentsRepository.saveAll(invoicePayments);
        log.info("Reconciliation process successfully finished for customer id: {}", dto.getCustomerId());

        Set<Invoice> uniqueInvoices = new LinkedHashSet<>(invoicesResult.invoices());
        List<Invoice> paidInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.PAID);
        List<Invoice> partiallyPaidInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.PARTIALLY_PAID);
        List<Invoice> outstandingInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.OUTSTANDING);
        List<Invoice> disputedInvoices = filterInvoicesByStatus(matchedInvoices, InvoiceStatus.DISPUTED);


        return new ReconciliationProcessResponse(
                uniqueInvoices.size(),
                matchedInvoices.size(),
                noMatchInvoices.size(),
                matchedTransactions.size(),
                noMatchTransaction.size(),
                paidInvoices.size(),
                partiallyPaidInvoices.size(),
                outstandingInvoices.size(),
                disputedInvoices.size()
        );
    }

    /**
     * Processes the uploaded bank statement Excel file using
     * {@link #excelBankTransactionReaderNgb#processExcelFile(MultipartFile)}
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
     * @param transaction The {@link BankTransaction} to check
     * @param customer The {@link Customer} used for matching
     * @return {@code true} If the {@code SenderName} matches with the {@link Customer}, otherwise {@code false}
     */
    private boolean matchesCustomer(BankTransaction transaction, Customer customer) {
        if(transaction.getSenderName() == null) return false;

        String senderName = transaction.getSenderName().toUpperCase();

        if(customer.getCustomerType() == CustomerType.COMPANY) {
            return senderName.contains(customer.getCompanyName().toUpperCase());
        }

        String customerName = customer.getLastName().toUpperCase() + " " +  customer.getFirstName().toUpperCase();
        return senderName.contains(customerName);
    }

    /**
     * Returns a set of {@link Invoice} filtered by status
     * @param invoices The invoices to filter
     * @param status The {@link InvoiceStatus} to filter the invoices
     * @return A filtered {@link List} of {@link Invoice} by status
     */
    private List<Invoice> filterInvoicesByStatus(List<Invoice> invoices, InvoiceStatus status) {
        return invoices.stream().filter(i -> Objects.equals(i.getStatus(), status)).toList();
    }

    /**
     * Validates that the provided {@code file} has the {@code .xlsx} extension
     * @param file The file which will be validated
     * @throws BadRequestException If the file extension is invalid
     */
    private void validateFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if(fileName == null || !fileName.endsWith(".xlsx")) {
            throw new BadRequestException("Only .xlsx files are support", "UNSUPPORTED_FILE");
        }
    }
}
