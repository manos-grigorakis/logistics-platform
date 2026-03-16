package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.BadRequestException;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.BankStatementImportResultDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.excel.ExcelBankTransactionReaderNgb;
import com.manosgrigorakis.logisticsplatform.payments.dto.PrepareReconciliationResult;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.mapper.BankTransactionMapper;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.repository.BankTransactionRepository;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    private final InvoiceService invoiceService;
    private final ExcelBankTransactionReaderNgb excelBankTransactionReaderNgb;
    private final InvoiceRepository invoiceRepository;
    private final BankTransactionRepository bankTransactionRepository;

    private static final Logger log = LoggerFactory.getLogger(ReconciliationServiceImpl.class);
    private static final Integer MAX_INVOICES = 5;
    private static final Integer MAX_DAYS_RANGE = 60;

    public ReconciliationServiceImpl(InvoiceService invoiceService, ExcelBankTransactionReaderNgb excelBankTransactionReaderNgb, InvoiceRepository invoiceRepository, BankTransactionRepository bankTransactionRepository) {
        this.invoiceService = invoiceService;
        this.excelBankTransactionReaderNgb = excelBankTransactionReaderNgb;
        this.invoiceRepository = invoiceRepository;
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Override
    @Transactional
    public void reconciliationProcess(ReconciliationRequestDTO dto) {
        List<BankStatementImportResultDTO> bankResults;
        List<BankTransaction> bankTransactions;
        List<BankTransaction> matchedTransactions = new ArrayList<>();
        List<Invoice> matchedInvoices = new ArrayList<>();
        List<Invoice> noMatchInvoices = new ArrayList<>();
        List<BankTransaction> noMatchTransaction = new ArrayList<>();

        PrepareReconciliationResult invoicesResult =
                this.invoiceService.prepareInvoicesForReconciliation(dto.getCustomerId(), dto.getInvoiceFile());

        Customer customer = invoicesResult.customer();

        // Bank Statement file process
        try {
            bankResults = excelBankTransactionReaderNgb.readExcel(dto.getBankStatement());
            bankTransactions = bankResults.stream().map(result ->
                    BankTransactionMapper.toEntity(result, "NBG")).toList();

        } catch (IOException e) {
            log.info("Failed to process bank statement for {}", dto.getBankStatement().getOriginalFilename(), e);
            throw new BadRequestException("Processing Excel bank statement failed", "FAILED_TO_PROCESS_BANK");
        }

        // Filter transactions based on the customer name
        List<BankTransaction> filteredBankTransactions = bankTransactions.stream()
                .filter(t -> matchesCustomer(t, customer)).toList();

        // Transaction contains one single invoice number in description
        for (Invoice invoice : invoicesResult.invoices()) {
            String invoiceNumber = formatInvoiceNumber(invoice.getExternalInvoiceNumber());

            for (BankTransaction transaction : filteredBankTransactions) {
                log.info("Invoice Number {} | Transaction Description: {} |  Amount: {} | PAID: {}",
                        invoiceNumber, transaction.getDescription(), invoice.getTotalAmount(), transaction.getAmount());

                if(transaction.getDescription() != null && (transaction.getDescription().contains(invoiceNumber))) {
                    if (invoice.getTotalAmount().compareTo(transaction.getAmount()) == 0) {
                        invoice.setStatus(InvoiceStatus.PAID);
                        invoice.setRemainingAmount(BigDecimal.ZERO);
                        log.info("Invoice {} match status PAID", invoiceNumber);
                    } else {
                        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
                        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(transaction.getAmount());
                        invoice.setRemainingAmount(remainingAmount);
                        log.info("Invoice {} match status PARTIALLY_PAID | Amount: {}, Remaining: {}",
                                invoiceNumber, invoice.getTotalAmount(), remainingAmount);
                    }

                    // Save matched results to lists
                    matchedInvoices.add(invoice);
                    matchedTransactions.add(transaction);

                    // TODO: Relationships connection
                }
            }
            if(!matchedInvoices.contains(invoice)) {
                noMatchInvoices.add(invoice);
            }
        }

        filteredBankTransactions.stream()
                .filter(t -> !matchedTransactions.contains(t))
                .forEach(noMatchTransaction::add);

        // One transaction multiple paid invoices
        for(BankTransaction t : noMatchTransaction) {
            List<Invoice> potentialInvoiceMatch = new ArrayList<>();
            List<Invoice> combination = new ArrayList<>();

            // Finds the potential invoices based on days range
            for (Invoice i : noMatchInvoices) {
                LocalDate maxInvoiceDateRange = i.getInvoiceDate().plusDays(MAX_DAYS_RANGE);

                // These are the potential invoices filtered based on the days range
                if (!t.getIssueDate().isBefore(i.getInvoiceDate()) &&
                        (t.getIssueDate().isBefore(maxInvoiceDateRange) || t.getIssueDate().equals(maxInvoiceDateRange))
                ) {
                    potentialInvoiceMatch.add(i);
                }
            }

            boolean matchFound = calculateSubsetSum(
                    0,
                    0,
                    t.getAmount(),
                    BigDecimal.ZERO,
                    potentialInvoiceMatch,
                    combination
            );

            if(matchFound) {
                for(Invoice invoice : combination) {
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoice.setRemainingAmount(BigDecimal.ZERO);
                    matchedInvoices.add(invoice);
                }
                matchedTransactions.add(t);
            }
        }

        // TODO: More rules
        // One transaction with multiple invoices while the invoices numbers included in transaction description

        invoiceRepository.saveAll(matchedInvoices);
        bankTransactionRepository.saveAll(matchedTransactions);
        log.info("Operation was success");
    }

    /**
     * Formats an {@link Invoice} number to retrieve only the actual number,
     * by splitting the code and removing any zero prefixes
     * @param invoiceNumber The invoice number to be formatted
     * @return The formatted invoice number (e.g. ΤΠΥM-0000000700 -> 700)
     */
    private static String formatInvoiceNumber(String invoiceNumber) {
        String[] splitInvoiceNumber = invoiceNumber.split("-");
        return String.valueOf(Integer.parseInt(splitInvoiceNumber[1]));
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
     * Finds a valid combination of invoices whose sum equals to the target amount using recursion
     * <p><b>Recursion Constraints to Stop</b></p>
     * <ul>
     *     <li>Target is null {@code false}</li>
     *     <li>Combination found {@code true}</li>
     *     <li>Current index >= candidates size prevents AIOBE {@code false}</li>
     *     <li>Current Count >= {@link #MAX_INVOICES} constraint {@code false}</li>
     *     <li>Current Sum > target {@code false}</li>
     * </ul>
     * @param currentIndex Indexer used to track invoices
     * @param currentCount Counter used to ensure the {@link #MAX_INVOICES} constraints is followed
     * @param target The target amount from the {@link BankTransaction}
     * @param currentSum The current sum of the selected invoices
     * @param candidates The candidates invoice to be checked
     * @param currentCombination A {@link List} containing the founded combination from the process
     * @return {@code true} If a valid combination is found, otherwise {code false}
     */
    private static boolean calculateSubsetSum(
            int currentIndex,
            int currentCount,
            BigDecimal target,
            BigDecimal currentSum,
            List<Invoice> candidates,
            List<Invoice> currentCombination
    ) {
        if (target == null) return false;
        if(currentSum.compareTo(target) == 0) return true;
        if(currentIndex >= candidates.size()) return false;
        if(currentCount >= MAX_INVOICES) return false;
        if(currentSum.compareTo(target) > 0) return false;

        Invoice invoice = candidates.get(currentIndex);

        // Include first invoice
        currentCombination.add(candidates.get(currentIndex));
        if(
                calculateSubsetSum(
                        currentIndex + 1,
                        currentCount + 1,
                        target,
                        currentSum.add(invoice.getTotalAmount()),
                        candidates,
                        currentCombination
                )
        ) {
            return true;
        }

        // If there is no combination from the above operation
        currentCombination.remove(currentCombination.size() - 1);

        // Don't include the first invoice
        return calculateSubsetSum(currentIndex + 1, currentCount, target, currentSum, candidates, currentCombination);
    }
}
