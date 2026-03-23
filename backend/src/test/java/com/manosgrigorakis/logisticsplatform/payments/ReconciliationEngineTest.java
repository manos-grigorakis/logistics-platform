package com.manosgrigorakis.logisticsplatform.payments;

import com.manosgrigorakis.logisticsplatform.payments.dto.InvoiceMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.dto.MultipleInvoicesMatchingResults;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.utility.ReconciliationEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReconciliationEngineTest {
    @Test
    void invoiceNumberDeclared_shouldFindMatches() {
        // Arrange
        List<Invoice> invoices = new ArrayList<>();
        List<BankTransaction> bankTransactions = new ArrayList<>();

        invoices.add(new Invoice(
                "INVOICE-0000500", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-01-20")));
        invoices.add(new Invoice(
                "INVOICE-0000501", InvoiceStatus.OUTSTANDING,
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1000.00), LocalDate.parse("2026-01-25")));
        invoices.add(new Invoice(
                "INVOICE-0000502", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-10")));

        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(1000.00),
                "INVOICE-0000500", LocalDate.parse("2026-02-20")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(1000.00), "501",
                LocalDate.parse("2026-02-20")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(800.00), "502",
                LocalDate.parse("2026-02-20")));

        // Act
        InvoiceMatchingResults results = ReconciliationEngine.invoiceNumberDeclared(invoices, bankTransactions);

        // Assert
        assertEquals(3, results.matchedInvoices().size());
        assertEquals(3, results.matchedTransactions().size());
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000500", InvoiceStatus.PAID));
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000501", InvoiceStatus.PAID));
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000502", InvoiceStatus.PARTIALLY_PAID));

        assertEquals(0, results.noMatchInvoices().size());
    }

    @Test
    void multipleInvoiceNumberDeclared_shouldFindMatches() {
        // Arrange
        List<Invoice> invoices = new ArrayList<>();
        List<BankTransaction> bankTransactions = new ArrayList<>();

        invoices.add(new Invoice(
                "INVOICE-0000503", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-15")));
        invoices.add(new Invoice(
                "INVOICE-0000504", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(2000.00),
                "INVOICE-503, INVOICE-504", LocalDate.parse("2026-03-20")));

        // Act
        MultipleInvoicesMatchingResults results = ReconciliationEngine.
                multipleInvoicesDeclared(invoices, bankTransactions);

        // Assert
        assertEquals(2, results.matchedInvoices().size());
        assertEquals(1, results.matchedTransactions().size());
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000503", InvoiceStatus.PAID));
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000504", InvoiceStatus.PAID));
    }

    @Test
    void multipleInvoices_shouldFindMatches() {
        // Arrange
        List<Invoice> invoices = new ArrayList<>();
        List<BankTransaction> bankTransactions = new ArrayList<>();
        invoices.add(new Invoice(
                "INVOICE-0000505", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        invoices.add(new Invoice(
                "INVOICE-0000506", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(2000.00), "",
                LocalDate.parse("2026-03-25")));

        // Act
        MultipleInvoicesMatchingResults results = ReconciliationEngine.multipleInvoices(bankTransactions, invoices);

        // Assert
        assertEquals(2, results.matchedInvoices().size());
        assertEquals(1, results.matchedTransactions().size());
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000505", InvoiceStatus.PAID));
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000506", InvoiceStatus.PAID));
    }

    @Test
    void multipleInvoices_shouldMatch_withCorrectCombination() {
        // Arrange
        List<Invoice> invoices = new ArrayList<>();
        List<BankTransaction> bankTransactions = new ArrayList<>();

        invoices.add(new Invoice(
                "INVOICE-0000505", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1500.00),
                BigDecimal.valueOf(1500.00), LocalDate.parse("2026-02-25")));
        invoices.add(new Invoice(
                "INVOICE-0000506", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        invoices.add(new Invoice(
                "INVOICE-0000507", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(2000.00), "",
                LocalDate.parse("2026-03-25")));

        // Act
        MultipleInvoicesMatchingResults results = ReconciliationEngine.multipleInvoices(bankTransactions, invoices);

        // Assert
        assertEquals(2, results.matchedInvoices().size());
        assertEquals(1, results.matchedTransactions().size());
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000506", InvoiceStatus.PAID));
        assertTrue(hasInvoice(results.matchedInvoices(), "INVOICE-0000507", InvoiceStatus.PAID));
    }

    @Test
    void multipleInvoices_shouldNotMatch_whenAmountNotMatch() {
        // Arrange
        List<Invoice> invoices = new ArrayList<>();
        List<BankTransaction> bankTransactions = new ArrayList<>();

        invoices.add(new Invoice(
                "INVOICE-0000505", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1500.00),
                BigDecimal.valueOf(1500.00), LocalDate.parse("2026-02-25")));
        invoices.add(new Invoice(
                "INVOICE-0000506", InvoiceStatus.OUTSTANDING, BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1000.00), LocalDate.parse("2026-02-25")));
        bankTransactions.add(new BankTransaction(
                "NBG", "ACME Logistics", BigDecimal.valueOf(2000.00), "",
                LocalDate.parse("2026-03-25")));

        // Act
        MultipleInvoicesMatchingResults results = ReconciliationEngine.multipleInvoices(bankTransactions, invoices);

        // Assert
        assertEquals(0, results.matchedInvoices().size());
        assertEquals(0, results.matchedTransactions().size());
    }

    /**
     * Verifies that the {@link Invoice} exists with the provided {@code invoiceNumber} and has the {@code status}
     * in the provided {@link List} of {@link Invoice}
     * @param invoices The {@link List} of {@link Invoice} to search for matches
     * @param invoiceNumber The {@link Invoice} number to check
     * @param status The status of the {@link Invoice}
     * @return {@code true} If there is a match, otherwise {@code false}
     */
    private boolean hasInvoice(List<Invoice> invoices, String invoiceNumber, InvoiceStatus status) {
        return invoices.stream().anyMatch(i -> i.getExternalInvoiceNumber().
                equals(invoiceNumber) && i.getStatus() == status);
    }
}
