package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ExcelBankTransactionReaderNgb;
import com.manosgrigorakis.logisticsplatform.payments.dto.InvoiceMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.dto.MultipleInvoicesMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.dto.PrepareReconciliationResult;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
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
import java.util.ArrayList;
import java.util.List;

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
    public void reconciliationProcess(ReconciliationRequestDTO dto) {
        List<BankTransaction> noMatchTransaction = new ArrayList<>();

        PrepareReconciliationResult invoicesResult =
                this.invoiceService.prepareInvoicesForReconciliation(dto.getCustomerId(), dto.getInvoiceFile());

        Customer customer = invoicesResult.customer();

        // Process bank statement file
        List<BankTransaction> bankTransactions = new ArrayList<>(processExcelFile(dto.getBankStatement()));

        // Filter transactions based on the customer name
        List<BankTransaction> filteredBankTransactions = bankTransactions.stream()
                .filter(t -> matchesCustomer(t, customer)).toList();

        // Transaction contains one single invoice number in description
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

        // One transaction pays multiple invoices
        MultipleInvoicesMatchingResults multipleInvoicesMatchResult = ReconciliationEngine
                .multipleInvoices(noMatchTransaction, noMatchInvoices);

        matchedInvoices.addAll(multipleInvoicesMatchResult.matchedInvoices());
        matchedTransactions.addAll(multipleInvoicesMatchResult.matchedTransactions());
        invoicePayments.addAll(multipleInvoicesMatchResult.invoicePayments());

        // TODO: One transaction with multiple invoices while the invoices numbers included in transaction description

        invoiceRepository.saveAll(matchedInvoices);
        bankTransactionRepository.saveAll(matchedTransactions);
        invoicePaymentsRepository.saveAll(invoicePayments);
        log.info("Operation was success");
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
}
