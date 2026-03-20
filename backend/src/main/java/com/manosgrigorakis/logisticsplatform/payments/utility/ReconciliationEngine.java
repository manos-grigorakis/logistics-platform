package com.manosgrigorakis.logisticsplatform.payments.utility;

import com.manosgrigorakis.logisticsplatform.payments.dto.InvoiceMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.dto.MultipleInvoicesMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.model.InvoicePayments;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ReconciliationEngine {
    private static final Integer MAX_INVOICES = 5;
    private static final Integer MAX_DAYS_RANGE = 60;

    /**
     * Handles the case where an invoice is paid fully or partially by a single transaction
     * that explicitly contains the invoice number in its description.
     * @param invoices The invoices to evaluate
     * @param bankTransactions The transactions to process
     * @return {@link InvoiceMatchingResults} containing the matched results
     */
    public static InvoiceMatchingResults invoiceNumberDeclared(
            List<Invoice> invoices,
            List<BankTransaction> bankTransactions
    ) {
        List<Invoice> matchedInvoices = new ArrayList<>();
        List<Invoice> noMatchInvoices = new ArrayList<>();
        List<BankTransaction> matchedTransactions = new ArrayList<>();
        List<InvoicePayments> invoicePayments = new ArrayList<>();

        for (Invoice invoice : invoices) {
            String invoiceNumber = formatInvoiceNumber(invoice.getExternalInvoiceNumber());

            for (BankTransaction transaction : bankTransactions) {
                String description = transaction.getDescription();
                if(description == null) continue;

                if(description.contains(invoiceNumber)) {
                    // Possible multiple invoices in the description = skip
                    if(findInvoicesByDescription(invoices, description).size() > 1) continue;

                    if (invoice.getTotalAmount().compareTo(transaction.getAmount()) == 0) {
                        invoice.setStatus(InvoiceStatus.PAID);
                        invoice.setRemainingAmount(BigDecimal.ZERO);
                    } else {
                        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
                        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(transaction.getAmount());
                        invoice.setRemainingAmount(remainingAmount);
                    }

                    // Add matched results to lists
                    matchedInvoices.add(invoice);
                    matchedTransactions.add(transaction);
                    invoicePayments.add(new InvoicePayments(invoice, transaction, transaction.getAmount()));

                    break;
                }
            }
            if(!matchedInvoices.contains(invoice)) {
                noMatchInvoices.add(invoice);
            }
        }
        return new InvoiceMatchingResults(matchedInvoices, noMatchInvoices, matchedTransactions, invoicePayments);
    }

    /**
     * Handles the case where a transaction includes in its description multiple {@link Invoice} numbers
     * (e.g. comma or whitespace separation)
     * @param invoices         The invoices to evaluate
     * @param bankTransactions The transactions to process
     * @return {@link MultipleInvoicesMatchingResults} containing the matched results
     */
    public static MultipleInvoicesMatchingResults multipleInvoicesDeclared(
            List<Invoice> invoices,
            List<BankTransaction> bankTransactions
    ) {
        List<Invoice> matchedInvoices = new ArrayList<>();
        List<BankTransaction> matchedTransactions = new ArrayList<>();
        List<InvoicePayments> invoicePayments = new ArrayList<>();

        for (BankTransaction transaction : bankTransactions) {
            if (transaction.getDescription() != null) {
                // Commas & whitespaces
                String[] formattedDescription = transaction.getDescription().split("[,\\s]+");
                List<String> invoiceNumbers = new ArrayList<>();

                for (String s : formattedDescription) {
                    if (!s.contains("-")) continue;
                    invoiceNumbers.add(formatInvoiceNumber(s));
                }

                for (Invoice invoice : invoices) {
                    String invoiceNumber = formatInvoiceNumber(invoice.getExternalInvoiceNumber());

                    for (String number : invoiceNumbers) {
                        if (Objects.equals(invoiceNumber, number)) {
                            invoice.setStatus(InvoiceStatus.PAID);
                            invoice.setRemainingAmount(BigDecimal.ZERO);

                            matchedInvoices.add(invoice);
                            matchedTransactions.add(transaction);
                            invoicePayments.add(new InvoicePayments(invoice, transaction, invoice.getTotalAmount()));
                            break;
                        }
                    }
                }
            }
        }
        return new MultipleInvoicesMatchingResults(matchedInvoices, matchedTransactions, invoicePayments);
    }

    /**
     * Handles the case where a single transaction pays multiple invoices,
     * without explicitly specifying which invoices are paid.
     * <p><b>Method Steps:</b></p>
     * <ul>
     *     <li>Finds candidates invoices based on transaction date and allowed invoice date range</li>
     *     <li>Attempts to find a combination of invoices whose total amount matches the transaction amount</li>
     *     <li>If a match is found, updates the corresponding invoices and creates payment records</li>
     * </ul>
     * @param bankTransactions The transactions to process
     * @param invoices The invoices to evaluate
     * @return {@link MultipleInvoicesMatchingResults} containing the matching results
     */
    public static MultipleInvoicesMatchingResults multipleInvoices(
            List<BankTransaction> bankTransactions,
            List<Invoice> invoices
    ) {
        List<Invoice> matchedInvoices = new ArrayList<>();
        List<BankTransaction> matchedTransactions = new ArrayList<>();
        List<InvoicePayments> invoicePayments = new ArrayList<>();

        // One transaction multiple paid invoices
        for(BankTransaction transaction : bankTransactions) {
            // Candidate invoices filtered by max days range
            List<Invoice> potentialInvoiceMatch = invoices.stream()
                    .filter(invoice -> isWithinInvoiceDateRange(transaction, invoice))
                    .toList();

            List<Invoice> matchingInvoices = new ArrayList<>();

            boolean matchFound = calculateSubsetSum(
                    0,
                    0,
                    transaction.getAmount(),
                    BigDecimal.ZERO,
                    potentialInvoiceMatch,
                    matchingInvoices
            );

            if(matchFound) {
                for(Invoice invoice : matchingInvoices) {
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoice.setRemainingAmount(BigDecimal.ZERO);
                    matchedInvoices.add(invoice);
                    invoicePayments.add(new InvoicePayments(invoice, transaction, invoice.getTotalAmount()));
                }
                matchedTransactions.add(transaction);
            }
        }
        return new MultipleInvoicesMatchingResults(matchedInvoices, matchedTransactions, invoicePayments);
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
     * Validates that the transaction date is within the allowed date range of the invoice
     * @param transaction The transaction to validated
     * @param invoice The invoice used for validation
     * @return {@code true} If the transaction date is within the invoice's allowed range, otherwise {@code false}
     */
    private static boolean isWithinInvoiceDateRange(BankTransaction transaction, Invoice invoice) {
        LocalDate transactionDate = transaction.getIssueDate();
        LocalDate invoiceDate = invoice.getInvoiceDate();
        LocalDate maxDate = invoiceDate.plusDays(MAX_DAYS_RANGE);

        return !transactionDate.isBefore(invoiceDate) && !transactionDate.isAfter(maxDate);
    }

    /**
     * Filters invoices by formatting their numbers using {@link #formatInvoiceNumber(String)}
     * and checking if they exist in the provided description
     * @param invoices The list of the invoices to evaluate
     * @param description The transaction description
     * @return A {@link List} of invoices whose numbers are found in the description
     */
    private static List<Invoice> findInvoicesByDescription(List<Invoice> invoices, String description) {
        return invoices.stream()
                .filter(i -> {
                    String number = formatInvoiceNumber(i.getExternalInvoiceNumber());
                    return description.contains(number);
                }).toList();
    }
}
